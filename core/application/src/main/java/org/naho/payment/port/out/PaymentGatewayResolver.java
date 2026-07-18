package org.naho.payment.port.out;

import org.naho.payment.type.PaymentProvider;

public interface PaymentGatewayResolver {
    PaymentGatewayPort resolve(PaymentProvider provider);
}
