package org.naho.payment.usecase;

import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.in.GetPaymentInputPort;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.exception.ApplicationException;

import org.naho.shared.port.out.TransactionPort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class GetPaymentUseCase implements GetPaymentInputPort {

    private final PaymentOrderRepositoryPort orderRepositoryPort;
    private final TransactionPort transactionPort;

    public GetPaymentUseCase(PaymentOrderRepositoryPort orderRepositoryPort,
                             TransactionPort transactionPort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public PaymentOrderResult getPaymentByOrderCode(String orderCode) {
        return transactionPort.execute(() -> doGetPaymentByOrderCode(orderCode));
    }

    private PaymentOrderResult doGetPaymentByOrderCode(String orderCode) {
        PaymentOrder order = orderRepositoryPort.findByOrderCode(orderCode)
                .orElseThrow(() -> new ApplicationException(
                        PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND,
                        PaymentDetailMessageKey.PAYMENT_ORDER_NOT_FOUND));

        Instant now = Instant.now();
        if (order.getStatus() == PaymentStatus.PENDING && order.isExpiredAt(now)) {
            order.expire(now);
            orderRepositoryPort.save(order);
        }

        return mapToResult(order);
    }

    @Override
    public List<PaymentOrderResult> getPaymentsByUserId(Long userId) {
        return transactionPort.execute(() -> doGetPaymentsByUserId(userId));
    }

    private List<PaymentOrderResult> doGetPaymentsByUserId(Long userId) {
        List<PaymentOrder> orders = orderRepositoryPort.findAllByUserId(userId);
        Instant now = Instant.now();
        List<PaymentOrderResult> results = new ArrayList<>();
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
