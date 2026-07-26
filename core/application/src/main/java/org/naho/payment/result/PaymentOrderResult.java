package org.naho.payment.result;

import org.naho.payment.model.Money;
import org.naho.payment.type.PaymentProvider;
import org.naho.payment.type.PaymentStatus;

import java.time.Instant;

public record PaymentOrderResult(
        Long id,
        String orderCode,
        Long userId,
        Long subscriptionPlanId,
        Money amount,
        PaymentProvider provider,
        PaymentStatus status,
        String providerTransactionId,
        Instant createdTime,
        Instant expiresTime,
        Instant paidTime,
        Instant modifiedTime) {
}
