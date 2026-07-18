package org.naho.payment.usecase;

import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.in.GetPaymentInputPort;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.shared.exception.ApplicationException;

public class GetPaymentUseCase implements GetPaymentInputPort {

    private final PaymentOrderRepositoryPort orderRepositoryPort;

    public GetPaymentUseCase(PaymentOrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public PaymentOrderResult getPaymentByOrderCode(String orderCode) {
        PaymentOrder order = orderRepositoryPort.findByOrderCode(orderCode)
                .orElseThrow(() -> new ApplicationException(
                        PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND,
                        "payment.order.not_found"));

        return new PaymentOrderResult(
                order.getId(),
                order.getOrderCode(),
                order.getUserId(),
                order.getSubscriptionPlanId(),
                order.getAmount(),
                order.getProvider(),
                order.getStatus(),
                order.getProviderTransactionId(),
                order.getCreatedTime(),
                order.getExpiresTime(),
                order.getPaidTime(),
                order.getModifiedTime());
    }
}
