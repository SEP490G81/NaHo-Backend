package org.naho.payment.port.in;

import org.naho.pagination.PageData;
import org.naho.payment.command.PaymentOrderQueryCommand;
import org.naho.payment.result.PaymentOrderResult;

import java.util.List;

public interface GetPaymentInputPort {
    PaymentOrderResult getPaymentByOrderCode(String orderCode);

    List<PaymentOrderResult> getPaymentsByUserId(Long userId);

    PageData<PaymentOrderResult> getAllPaymentOrders(PaymentOrderQueryCommand command);
}

