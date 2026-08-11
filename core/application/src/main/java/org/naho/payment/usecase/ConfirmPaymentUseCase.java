package org.naho.payment.usecase;

import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.payment.command.ConfirmPaymentCommand;
import org.naho.payment.event.PaymentConfirmedEvent;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.model.PaymentTransaction;
import org.naho.payment.port.in.ConfirmPaymentInputPort;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.port.out.PaymentTransactionRepositoryPort;
import org.naho.payment.result.ConfirmPaymentResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.EventPublisherPort;
import org.naho.shared.port.out.TransactionPort;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.user.event.UserPlanUpgradedEvent;

import java.time.Instant;

public class ConfirmPaymentUseCase implements ConfirmPaymentInputPort {

    private final PaymentOrderRepositoryPort orderRepositoryPort;
    private final PaymentTransactionRepositoryPort transactionRepositoryPort;
    private final SubscriptionPlanRepositoryPort planRepositoryPort;
    private final UserSubscriptionRepositoryPort subscriptionRepositoryPort;
    private final EventPublisherPort eventPublisherPort;
    private final TransactionPort transactionPort;

    public ConfirmPaymentUseCase(PaymentOrderRepositoryPort orderRepositoryPort,
                                 PaymentTransactionRepositoryPort transactionRepositoryPort,
                                 SubscriptionPlanRepositoryPort planRepositoryPort,
                                 UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                                 EventPublisherPort eventPublisherPort,
                                 TransactionPort transactionPort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.planRepositoryPort = planRepositoryPort;
        this.subscriptionRepositoryPort = subscriptionRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public ConfirmPaymentResult confirmPayment(ConfirmPaymentCommand command) {
        return transactionPort.execute(() -> doConfirmPayment(command));
    }

    private ConfirmPaymentResult doConfirmPayment(ConfirmPaymentCommand command) {
        Instant now = Instant.now();

        if (transactionRepositoryPort.existsByProviderAndTransactionId(
                command.provider(),
                command.providerTransactionId())) {
            return ConfirmPaymentResult.duplicate(command.orderCode());
        }

        PaymentOrder order = orderRepositoryPort.findByOrderCodeForUpdate(command.orderCode())
                .orElseThrow(() -> new ApplicationException(
                        PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND,
                        PaymentDetailMessageKey.PAYMENT_ORDER_NOT_FOUND));

        if (order.isPaid()) {
            return ConfirmPaymentResult.alreadyPaid(order.getOrderCode());
        }

        PaymentTransaction transaction = PaymentTransaction.received(
                order.getId(),
                command.provider(),
                command.providerTransactionId(),
                command.paidAmount(),
                command.successful(),
                command.providerTransactionTime(),
                command.metadata(),
                now);

        transactionRepositoryPort.save(transaction);

        if (!command.successful()) {
            if (order.isExpiredAt(now)) {
                order.expire(now); // Convert to EXPIRED if past 5 minutes
            } else {
                order.markFailed(now); // Convert to FAILED if within 5 minutes
                // (e.g., wrong OTP, insufficient funds)
            }
            orderRepositoryPort.save(order);
            return ConfirmPaymentResult.failed(order.getOrderCode());
        }

        order.markPaid(
                command.providerTransactionId(),
                command.paidAmount(),
                now);

        SubscriptionPlan plan = planRepositoryPort.findById(order.getSubscriptionPlanId())
                .orElseThrow(() -> new ApplicationException(
                        PaymentErrorCode.PLAN_NOT_FOUND,
                        SubscriptionDetailMessageKey.PLAN_NOT_FOUND));

        if (!subscriptionRepositoryPort.existsByPaymentOrderId(order.getId())) {
            // Hủy gói active cũ (nếu có) trước khi kích hoạt gói mới
            subscriptionRepositoryPort.findActiveByUserId(order.getUserId(), now)
                    .ifPresent(previousSub -> {
                        previousSub.cancel(now);
                        subscriptionRepositoryPort.save(previousSub);
                    });

            UserSubscription subscription = UserSubscription.activate(
                    order.getUserId(),
                    plan.getId(),
                    order.getId(),
                    plan.getDurationDays(),
                    now);
            subscriptionRepositoryPort.save(subscription);

            // Publish Event Nâng cấp gói
            eventPublisherPort.publish(new UserPlanUpgradedEvent(
                    order.getUserId(),
                    plan.getCode().name()));

            eventPublisherPort.publish(new PaymentConfirmedEvent(
                    order.getUserId(),
                    order.getId(),
                    order.getOrderCode(),
                    plan.getName()
            ));
        }

        orderRepositoryPort.save(order);

        return ConfirmPaymentResult.success(order.getOrderCode());
    }
}