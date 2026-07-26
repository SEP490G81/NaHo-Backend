package org.naho.payment.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.payment.entity.PaymentIdempotencyEntity;
import org.naho.payment.mapper.PaymentIdempotencyEntityMapper;
import org.naho.payment.model.PaymentIdempotency;
import org.naho.payment.port.out.PaymentIdempotencyRepositoryPort;
import org.naho.payment.repository.PaymentIdempotencyJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentIdempotencyRepositoryAdapter implements PaymentIdempotencyRepositoryPort {

    private final PaymentIdempotencyJpaRepository idempotencyJpaRepository;
    private final PaymentIdempotencyEntityMapper idempotencyEntityMapper;

    @Override
    public Optional<PaymentIdempotency> findByUserIdAndKey(Long userId, String idempotencyKey) {
        return idempotencyJpaRepository.findByUserIdAndIdempotencyKey(userId, idempotencyKey)
                .map(idempotencyEntityMapper::entityToDomain);
    }

    @Override
    public PaymentIdempotency save(PaymentIdempotency idempotency) {
        PaymentIdempotencyEntity entity = idempotencyEntityMapper.domainToEntity(idempotency);
        PaymentIdempotencyEntity savedEntity = idempotencyJpaRepository.save(entity);
        return idempotencyEntityMapper.entityToDomain(savedEntity);
    }
}
