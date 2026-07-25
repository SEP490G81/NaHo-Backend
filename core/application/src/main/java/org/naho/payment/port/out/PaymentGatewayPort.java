package org.naho.payment.port.out;

import org.naho.payment.model.PaymentOrder;
import org.naho.payment.type.PaymentProvider;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

public interface PaymentGatewayPort {
    PaymentProvider supportedProvider();

    PaymentInitializationResult initialize(
            PaymentOrder paymentOrder,
            PaymentCustomerContext customerContext);

    boolean verifySignature(Map<String, String> fields, String secureHash);

    record PaymentInitializationResult(
            URI paymentUrl,
            Instant expiresTime,
            Map<String, String> metadata) {
    }

    record PaymentCustomerContext(
            Long userId,
            String clientIp,
            String locale) {
    }
}
