package org.naho.payment.adapter;

import org.naho.payment.port.out.PaymentOrderCodeGenerator;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDPaymentOrderCodeGenerator implements PaymentOrderCodeGenerator {

    @Override
    public String generate() {
        String rawUuid = UUID.randomUUID().toString().replace("-", "");
        return "NHPAY" + rawUuid.substring(0, 10).toUpperCase();
    }
}
