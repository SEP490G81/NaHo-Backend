package org.naho.payment.port.out;

import org.naho.payment.model.PaymentIdempotency;

import java.util.Optional;

public interface PaymentIdempotencyRepositoryPort {
    Optional<PaymentIdempotency> findByUserIdAndKey(Long userId, String idempotencyKey);

    PaymentIdempotency save(PaymentIdempotency idempotency);
}
