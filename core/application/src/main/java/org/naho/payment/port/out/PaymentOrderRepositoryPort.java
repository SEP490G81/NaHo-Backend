package org.naho.payment.port.out;

import org.naho.payment.model.PaymentOrder;
import java.util.Optional;

public interface PaymentOrderRepositoryPort {
    PaymentOrder save(PaymentOrder paymentOrder);

    Optional<PaymentOrder> findById(Long id);

    Optional<PaymentOrder> findByOrderCode(String orderCode);

    Optional<PaymentOrder> findByOrderCodeForUpdate(String orderCode);

    boolean existsByOrderCode(String orderCode);
}
