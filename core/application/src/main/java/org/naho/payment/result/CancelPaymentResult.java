package org.naho.payment.result;

import org.naho.payment.type.PaymentStatus;

public record CancelPaymentResult(
        String orderCode,
        PaymentStatus status
) {
    public static CancelPaymentResult success(String orderCode) {
        return new CancelPaymentResult(orderCode, PaymentStatus.CANCELLED);
    }
}
