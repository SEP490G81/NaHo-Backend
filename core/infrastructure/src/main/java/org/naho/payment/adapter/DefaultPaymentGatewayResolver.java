package org.naho.payment.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.payment.port.out.PaymentGatewayPort;
import org.naho.payment.port.out.PaymentGatewayResolver;
import org.naho.payment.type.PaymentProvider;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultPaymentGatewayResolver implements PaymentGatewayResolver {

    private final List<PaymentGatewayPort> gateways;

    @Override
    public PaymentGatewayPort resolve(PaymentProvider provider) {
        return gateways.stream()
                .filter(gateway -> gateway.supportedProvider() == provider)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported payment provider: " + provider));
    }
}
