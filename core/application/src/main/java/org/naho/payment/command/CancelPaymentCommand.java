package org.naho.payment.command;

public record CancelPaymentCommand(
        String orderCode,
        Long userId
) {}
