package org.naho.payment.result;

import org.naho.payment.type.ConfirmPaymentStatus;

public record ConfirmPaymentResult(
        ConfirmPaymentStatus status,
        String message,
        String orderCode) {
    public static ConfirmPaymentResult success(String orderCode) {
        return new ConfirmPaymentResult(ConfirmPaymentStatus.SUCCESS, "Payment confirmed successfully", orderCode);
    }

    public static ConfirmPaymentResult alreadyPaid(String orderCode) {
        return new ConfirmPaymentResult(ConfirmPaymentStatus.ALREADY_PAID, "Payment order was already confirmed", orderCode);
    }

    public static ConfirmPaymentResult duplicate(String orderCode) {
        return new ConfirmPaymentResult(ConfirmPaymentStatus.DUPLICATE, "Duplicate transaction ID received", orderCode);
    }

    public static ConfirmPaymentResult failed(String orderCode) {
        return new ConfirmPaymentResult(ConfirmPaymentStatus.FAILED, "Payment transaction marked as failed", orderCode);
    }
}
