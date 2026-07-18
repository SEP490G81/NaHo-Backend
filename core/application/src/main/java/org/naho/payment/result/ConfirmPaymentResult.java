package org.naho.payment.result;

public record ConfirmPaymentResult(
        String status,
        String message,
        String orderCode) {
    public static ConfirmPaymentResult success(String orderCode) {
        return new ConfirmPaymentResult("SUCCESS", "Payment confirmed successfully", orderCode);
    }

    public static ConfirmPaymentResult alreadyPaid(String orderCode) {
        return new ConfirmPaymentResult("ALREADY_PAID", "Payment order was already confirmed", orderCode);
    }

    public static ConfirmPaymentResult duplicate(String orderCode) {
        return new ConfirmPaymentResult("DUPLICATE", "Duplicate transaction ID received", orderCode);
    }

    public static ConfirmPaymentResult failed(String orderCode) {
        return new ConfirmPaymentResult("FAILED", "Payment transaction marked as failed", orderCode);
    }
}
