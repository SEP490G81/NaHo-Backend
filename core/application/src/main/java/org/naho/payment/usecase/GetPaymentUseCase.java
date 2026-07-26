package org.naho.payment.usecase;

import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.in.GetPaymentInputPort;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.exception.ApplicationException;

import java.time.Instant;

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

        Instant now = Instant.now();
        if (order.getStatus() == PaymentStatus.PENDING && order.isExpiredAt(now)) {
            order.expire(now);
            orderRepositoryPort.save(order);
        }

        return mapToResult(order);
    }

    @Override
    public java.util.List<PaymentOrderResult> getPaymentsByUserId(Long userId) {
        java.util.List<PaymentOrder> orders = orderRepositoryPort.findAllByUserId(userId);
        Instant now = Instant.now();
        java.util.List<PaymentOrderResult> results = new java.util.ArrayList<>();
        for (PaymentOrder order : orders) {
            if (order.getStatus() == PaymentStatus.PENDING && order.isExpiredAt(now)) {
                order.expire(now);
                orderRepositoryPort.save(order);
            }
            results.add(mapToResult(order));
        }
        return results;
    }

    private PaymentOrderResult mapToResult(PaymentOrder order) {
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

