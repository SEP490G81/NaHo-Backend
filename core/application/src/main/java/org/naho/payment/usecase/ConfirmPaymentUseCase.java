package org.naho.payment.usecase;

import org.naho.payment.command.ConfirmPaymentCommand;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.model.PaymentTransaction;
import org.naho.payment.port.in.ConfirmPaymentInputPort;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.port.out.PaymentTransactionRepositoryPort;
import org.naho.payment.result.ConfirmPaymentResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;

import java.time.Instant;

public class ConfirmPaymentUseCase implements ConfirmPaymentInputPort {

    private final PaymentOrderRepositoryPort orderRepositoryPort;
    private final PaymentTransactionRepositoryPort transactionRepositoryPort;
    private final SubscriptionPlanRepositoryPort planRepositoryPort;
    private final UserSubscriptionRepositoryPort subscriptionRepositoryPort;
    private final org.naho.shared.port.out.EventPublisherPort eventPublisherPort;

    public ConfirmPaymentUseCase(PaymentOrderRepositoryPort orderRepositoryPort,
                                 PaymentTransactionRepositoryPort transactionRepositoryPort,
                                 SubscriptionPlanRepositoryPort planRepositoryPort,
                                 UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                                 org.naho.shared.port.out.EventPublisherPort eventPublisherPort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.transactionRepositoryPort = transactionRepositoryPort;
        this.planRepositoryPort = planRepositoryPort;
        this.subscriptionRepositoryPort = subscriptionRepositoryPort;
        this.eventPublisherPort = eventPublisherPort;
    }

    @Override
    public ConfirmPaymentResult confirmPayment(ConfirmPaymentCommand command) {
        Instant now = Instant.now();

        if (transactionRepositoryPort.existsByProviderAndTransactionId(
                command.provider(),
                command.providerTransactionId())) {
            return ConfirmPaymentResult.duplicate(command.orderCode());
        }

        PaymentOrder order = orderRepositoryPort.findByOrderCodeForUpdate(command.orderCode())
                .orElseThrow(() -> new ApplicationException(
                        PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND,
                        "payment.order.not_found"));

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
                order.expire(now); // Chuyển trạng thái chuẩn sang EXPIRED nếu đã quá 5 phút
            } else {
                order.markFailed(now); // Chuyển sang FAILED nếu thất bại trong thời hạn 5 phút (ví dụ: sai OTP, tài
                // khoản không đủ tiền)
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
                        "subscription.plan.not_found"));

        if (!subscriptionRepositoryPort.existsByPaymentOrderId(order.getId())) {
            UserSubscription subscription = UserSubscription.activate(
                    order.getUserId(),
                    plan.getId(),
                    order.getId(),
                    plan.getDurationDays(),
                    now);
            subscriptionRepositoryPort.save(subscription);

            // Publish Event Nâng cấp gói
            eventPublisherPort.publish(new org.naho.user.event.UserPlanUpgradedEvent(
                    order.getUserId(),
                    plan.getCode()
            ));
        }

        orderRepositoryPort.save(order);

        return ConfirmPaymentResult.success(order.getOrderCode());
    }
}
