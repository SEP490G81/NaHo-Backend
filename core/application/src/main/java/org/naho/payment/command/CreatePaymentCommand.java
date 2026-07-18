package org.naho.payment.command;

import org.naho.payment.type.PaymentProvider;

public record CreatePaymentCommand(
                Long userId,
                String planCode,
                PaymentProvider provider,
                String clientIp,
                String locale) {
}
