package org.naho.payment.result;

import org.naho.payment.model.Money;
import org.naho.payment.type.PaymentReuseReason;
import org.naho.payment.type.PaymentStatus;

import java.net.URI;
import java.time.Instant;

public record CreatePaymentResult(
        Long paymentOrderId,
        String orderCode,
        Money amount,
        PaymentStatus status,
        URI paymentUrl,
        Instant expiresTime,
        boolean reused,
        PaymentReuseReason reuseReason) {
}
