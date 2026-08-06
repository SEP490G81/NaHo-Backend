package org.naho.payment.command;

import org.naho.payment.type.PaymentProvider;
import org.naho.subscription.type.PlanCode;

public record CreatePaymentCommand(
        Long userId,
        PlanCode planCode,
        PaymentProvider provider,
        String clientIp,
        String locale,
        String idempotencyKey) {
}
