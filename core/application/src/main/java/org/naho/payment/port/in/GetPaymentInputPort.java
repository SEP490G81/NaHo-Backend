package org.naho.payment.port.in;

import org.naho.payment.result.PaymentOrderResult;

import java.util.List;

public interface GetPaymentInputPort {
    PaymentOrderResult getPaymentByOrderCode(String orderCode);

    List<PaymentOrderResult> getPaymentsByUserId(Long userId);
}

