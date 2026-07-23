package org.naho.payment.command;

import org.naho.payment.model.Money;
import org.naho.payment.type.PaymentProvider;

import java.time.Instant;
import java.util.Map;

public record ConfirmPaymentCommand(
                PaymentProvider provider,
                String orderCode,
                String providerTransactionId,
                Money paidAmount,
                boolean successful,
                Instant providerTransactionTime,
                Map<String, String> metadata) {
}
