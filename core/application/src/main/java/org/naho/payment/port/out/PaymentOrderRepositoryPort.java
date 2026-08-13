package org.naho.payment.port.out;

import org.naho.pagination.PageData;
import org.naho.payment.command.PaymentOrderQueryCommand;
import org.naho.payment.model.PaymentOrder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepositoryPort {
    PaymentOrder save(PaymentOrder paymentOrder);

    Optional<PaymentOrder> findById(Long id);

    Optional<PaymentOrder> findByOrderCode(String orderCode);

    Optional<PaymentOrder> findByOrderCodeForUpdate(String orderCode);

    boolean existsByOrderCode(String orderCode);

    Optional<PaymentOrder> findPendingByUserId(Long userId);

    List<PaymentOrder> findAllByUserId(Long userId);

    PageData<PaymentOrder> findAllPaymentOrders(PaymentOrderQueryCommand command);

    void expirePendingBefore(Instant now);
}

