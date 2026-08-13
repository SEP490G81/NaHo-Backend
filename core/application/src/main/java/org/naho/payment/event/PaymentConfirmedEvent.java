package org.naho.payment.event;

public record PaymentConfirmedEvent(
        Long userId,
        Long paymentOrderId,
        String orderCode,
        String planName
) {
}
