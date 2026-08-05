package org.naho.payment.usecase;

import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.command.CancelPaymentCommand;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.in.CancelPaymentInputPort;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.result.CancelPaymentResult;
import org.naho.shared.exception.ApplicationException;

import java.time.Instant;
import java.util.Objects;

public class CancelPaymentUseCase implements CancelPaymentInputPort {

    private final PaymentOrderRepositoryPort orderRepositoryPort;

    public CancelPaymentUseCase(PaymentOrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public CancelPaymentResult cancelPayment(CancelPaymentCommand command) {
        Instant now = Instant.now();

        PaymentOrder order = orderRepositoryPort.findByOrderCode(command.orderCode())
                .orElseThrow(() -> new ApplicationException(
                        PaymentErrorCode.PAYMENT_ORDER_NOT_FOUND,
                        PaymentDetailMessageKey.PAYMENT_ORDER_NOT_FOUND));

        if (!Objects.equals(order.getUserId(), command.userId())) {
            throw new ApplicationException(
                    PaymentErrorCode.PAYMENT_INVALID_STATE,
                    PaymentDetailMessageKey.PAYMENT_ORDER_NOT_BELONG_TO_USER);
        }

        order.cancel(now);
        orderRepositoryPort.save(order);

        return CancelPaymentResult.success(order.getOrderCode());
    }
}
