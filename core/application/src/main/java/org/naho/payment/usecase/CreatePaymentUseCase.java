package org.naho.payment.usecase;

import org.naho.payment.command.CreatePaymentCommand;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.in.CreatePaymentInputPort;
import org.naho.payment.port.out.*;
import org.naho.payment.result.CreatePaymentResult;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.type.PlanTier;

import java.time.Duration;
import java.time.Instant;

public class CreatePaymentUseCase implements CreatePaymentInputPort {

    private static final Duration PAYMENT_EXPIRATION = Duration.ofMinutes(5);

    private final SubscriptionPlanRepositoryPort planRepositoryPort;
    private final PaymentOrderRepositoryPort paymentOrderRepositoryPort;
    private final UserSubscriptionRepositoryPort subscriptionRepositoryPort;
    private final PaymentGatewayResolver gatewayResolver;
    private final PaymentOrderCodeGenerator orderCodeGenerator;

    public CreatePaymentUseCase(SubscriptionPlanRepositoryPort planRepositoryPort,
                                PaymentOrderRepositoryPort paymentOrderRepositoryPort,
                                UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                                PaymentGatewayResolver gatewayResolver,
                                PaymentOrderCodeGenerator orderCodeGenerator) {
        this.planRepositoryPort = planRepositoryPort;
        this.paymentOrderRepositoryPort = paymentOrderRepositoryPort;
        this.subscriptionRepositoryPort = subscriptionRepositoryPort;
        this.gatewayResolver = gatewayResolver;
        this.orderCodeGenerator = orderCodeGenerator;
    }

    @Override
    public CreatePaymentResult createPayment(CreatePaymentCommand command) {
        Instant now = Instant.now();

        SubscriptionPlan plan = planRepositoryPort.findActiveByCode(command.planCode())
                .orElseThrow(() -> new ApplicationException(
                        SubscriptionErrorCode.PLAN_NOT_FOUND,
                        "subscription.plan.not_found"));

        if (!plan.isAvailableForPurchase()) {
            throw new ApplicationException(
                    SubscriptionErrorCode.PLAN_UNAVAILABLE,
                    "subscription.plan.unavailable");
        }

        if (plan.getTier() == PlanTier.FREE) {
            throw new ApplicationException(
                    SubscriptionErrorCode.PLAN_UNAVAILABLE,
                    "subscription.plan.free_not_purchasable");
        }

        subscriptionRepositoryPort.findActiveByUserId(command.userId(), now).flatMap
                (activeSub -> planRepositoryPort.findById(activeSub.getSubscriptionPlanId())).ifPresent
                (activePlan -> {
            if (activePlan.getTier()
                    .isHigherOrEqualThan(plan.getTier())) {
                throw new ApplicationException(
                        SubscriptionErrorCode.ALREADY_ACTIVE_HIGHER_OR_EQUAL_PLAN,
                        "subscription.plan.already_active_or_higher");
            }
        });

        String orderCode = orderCodeGenerator.generate();

        PaymentOrder order = PaymentOrder.create(
                orderCode,
                command.userId(),
                plan.getId(),
                plan.getPrice(),
                now,
                now.plus(PAYMENT_EXPIRATION));

        order.assignProvider(command.provider(), now);

        PaymentOrder savedOrder = paymentOrderRepositoryPort.save(order);

        PaymentGatewayPort gateway = gatewayResolver.resolve(command.provider());

        PaymentGatewayPort.PaymentInitializationResult initialization = gateway.initialize(
                savedOrder,
                new PaymentGatewayPort.PaymentCustomerContext(
                        command.userId(),
                        command.clientIp(),
                        command.locale()));

        return new CreatePaymentResult(
                savedOrder.getId(),
                savedOrder.getOrderCode(),
                savedOrder.getAmount(),
                savedOrder.getStatus(),
                initialization.paymentUrl(),
                initialization.expiresTime());
    }
}
