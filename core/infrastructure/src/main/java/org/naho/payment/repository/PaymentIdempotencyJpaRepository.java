package org.naho.payment.repository;

import org.naho.payment.entity.PaymentIdempotencyEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.Optional;

public interface PaymentIdempotencyJpaRepository extends BaseJpaRepository<PaymentIdempotencyEntity> {
    Optional<PaymentIdempotencyEntity> findByUserIdAndIdempotencyKey(Long userId, String idempotencyKey);
}
