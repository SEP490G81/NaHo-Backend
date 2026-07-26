package org.naho.payment.usecase;

import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.command.CreatePaymentCommand;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.PaymentIdempotency;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.in.CreatePaymentInputPort;
import org.naho.payment.port.out.*;
import org.naho.payment.result.CreatePaymentResult;
import org.naho.payment.type.PaymentReuseReason;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.type.PlanTier;
import org.naho.user.port.out.UserRepositoryPort;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class CreatePaymentUseCase implements CreatePaymentInputPort {

    private final SubscriptionPlanRepositoryPort planRepositoryPort;
    private final PaymentOrderRepositoryPort paymentOrderRepositoryPort;
    private final UserSubscriptionRepositoryPort subscriptionRepositoryPort;
    private final PaymentGatewayResolver gatewayResolver;
    private final PaymentOrderCodeGenerator orderCodeGenerator;
    private final UserRepositoryPort userRepositoryPort;
    private final PaymentIdempotencyRepositoryPort idempotencyRepositoryPort;
    private final int timeoutMinutes;

    public CreatePaymentUseCase(SubscriptionPlanRepositoryPort planRepositoryPort,
                                PaymentOrderRepositoryPort paymentOrderRepositoryPort,
                                UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                                PaymentGatewayResolver gatewayResolver,
                                PaymentOrderCodeGenerator orderCodeGenerator,
                                UserRepositoryPort userRepositoryPort,
                                PaymentIdempotencyRepositoryPort idempotencyRepositoryPort,
                                int timeoutMinutes) {
        this.planRepositoryPort = planRepositoryPort;
        this.paymentOrderRepositoryPort = paymentOrderRepositoryPort;
        this.subscriptionRepositoryPort = subscriptionRepositoryPort;
        this.gatewayResolver = gatewayResolver;
        this.orderCodeGenerator = orderCodeGenerator;
        this.userRepositoryPort = userRepositoryPort;
        this.idempotencyRepositoryPort = idempotencyRepositoryPort;
        this.timeoutMinutes = timeoutMinutes > 0 ? timeoutMinutes : 5;
    }

    @Override
    public CreatePaymentResult createPayment(CreatePaymentCommand command) {
        Instant now = Instant.now();

        validateIdempotencyKey(command.idempotencyKey());

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

        subscriptionRepositoryPort.findActiveByUserId(command.userId(), now)
                .flatMap(activeSub -> planRepositoryPort.findById(activeSub.getSubscriptionPlanId()))
                .ifPresent(activePlan -> {
                    if (activePlan.getTier().isHigherOrEqualThan(plan.getTier())) {
                        throw new ApplicationException(
                                SubscriptionErrorCode.ALREADY_ACTIVE_HIGHER_OR_EQUAL_PLAN,
                                "subscription.plan.already_active_or_higher");
                    }
                });

        String requestHash = createRequestHash(command);

        userRepositoryPort.lockById(command.userId());

        Optional<PaymentIdempotency> idempotencyOptional = idempotencyRepositoryPort.findByUserIdAndKey(
                command.userId(),
                command.idempotencyKey());

        if (idempotencyOptional.isPresent()) {
            PaymentIdempotency idempotency = idempotencyOptional.get();

            if (!idempotency.matchesRequestHash(requestHash)) {
                throw new ApplicationException(
                        PaymentErrorCode.IDEMPOTENCY_KEY_REUSED,
                        PaymentDetailMessageKey.PAYMENT_IDEMPOTENCY_KEY_REUSED);
            }

            PaymentOrder existingOrder = paymentOrderRepositoryPort
                    .findById(idempotency.getPaymentOrderId())
                    .orElseThrow(() -> new ApplicationException(
                            PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND,
                            PaymentDetailMessageKey.PAYMENT_ORDER_NOT_FOUND));

            return buildResult(existingOrder, true, PaymentReuseReason.IDEMPOTENCY_REPLAY);
        }

        Optional<PaymentOrder> pendingOptional = paymentOrderRepositoryPort.findPendingByUserId(command.userId());

        if (pendingOptional.isPresent()) {
            PaymentOrder pendingOrder = pendingOptional.get();

            if (pendingOrder.isExpiredAt(now)) {
                pendingOrder.expire(now);
                paymentOrderRepositoryPort.save(pendingOrder);
            } else {
                if (!pendingOrder.getSubscriptionPlanId().equals(plan.getId())
                        || pendingOrder.getProvider() != command.provider()) {
                    throw new ApplicationException(
                            PaymentErrorCode.PAYMENT_ACTIVE_ORDER_EXISTS,
                            PaymentDetailMessageKey.PAYMENT_ACTIVE_ORDER_EXISTS);
                }

                PaymentIdempotency mapping = PaymentIdempotency.create(
                        command.userId(),
                        command.idempotencyKey(),
                        requestHash,
                        pendingOrder.getId(),
                        now,
                        now.plus(Duration.ofHours(24)));

                idempotencyRepositoryPort.save(mapping);

                return buildResult(pendingOrder, true, PaymentReuseReason.ACTIVE_PENDING_REUSED);
            }
        }

        String orderCode = orderCodeGenerator.generate();
        Instant expiresTime = now.plus(Duration.ofMinutes(timeoutMinutes));

        PaymentOrder newOrder = PaymentOrder.create(
                orderCode,
                command.userId(),
                plan.getId(),
                plan.getPrice(),
                now,
                expiresTime);

        newOrder.assignProvider(command.provider(), now);

        PaymentOrder savedOrder = paymentOrderRepositoryPort.save(newOrder);

        PaymentGatewayPort gateway = gatewayResolver.resolve(command.provider());

        PaymentGatewayPort.PaymentInitializationResult initialization = gateway.initialize(
                savedOrder,
                new PaymentGatewayPort.PaymentCustomerContext(
                        command.userId(),
                        command.clientIp(),
                        command.locale()));

        savedOrder.assignPaymentUrl(initialization.paymentUrl().toString(), now);
        savedOrder = paymentOrderRepositoryPort.save(savedOrder);

        PaymentIdempotency mapping = PaymentIdempotency.create(
                command.userId(),
                command.idempotencyKey(),
                requestHash,
                savedOrder.getId(),
                now,
                now.plus(Duration.ofHours(24)));

        idempotencyRepositoryPort.save(mapping);

        return buildResult(savedOrder, false, PaymentReuseReason.CREATED);
    }

    private void validateIdempotencyKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank() || idempotencyKey.length() > 100) {
            throw new ApplicationException(
                    PaymentErrorCode.INVALID_IDEMPOTENCY_KEY,
                    PaymentDetailMessageKey.PAYMENT_IDEMPOTENCY_KEY_INVALID);
        }
    }

    private String createRequestHash(CreatePaymentCommand command) {
        String raw = command.userId() + "|" + command.planCode() + "|" + command.provider().name();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error calculating SHA-256 request hash", e);
        }
    }

    private CreatePaymentResult buildResult(PaymentOrder order, boolean reused, PaymentReuseReason reuseReason) {
        URI paymentUri = order.getPaymentUrl() != null && !order.getPaymentUrl().isBlank()
                ? URI.create(order.getPaymentUrl())
                : null;

        return new CreatePaymentResult(
                order.getId(),
                order.getOrderCode(),
                order.getAmount(),
                order.getStatus(),
                paymentUri,
                order.getExpiresTime(),
                reused,
                reuseReason);
    }
}
