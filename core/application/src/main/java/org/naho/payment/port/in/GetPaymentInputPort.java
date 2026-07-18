package org.naho.payment.port.in;

import org.naho.payment.result.PaymentOrderResult;

public interface GetPaymentInputPort {
    PaymentOrderResult getPaymentByOrderCode(String orderCode);
}
