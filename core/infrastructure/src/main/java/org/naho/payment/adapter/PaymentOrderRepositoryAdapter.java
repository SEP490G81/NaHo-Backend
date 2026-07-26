package org.naho.payment.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.payment.entity.PaymentOrderEntity;
import org.naho.payment.mapper.PaymentOrderEntityMapper;
import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.out.PaymentOrderRepositoryPort;
import org.naho.payment.repository.PaymentOrderJpaRepository;
import org.naho.subscription.entity.SubscriptionPlanEntity;
import org.naho.subscription.repository.SubscriptionPlanJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentOrderRepositoryAdapter implements PaymentOrderRepositoryPort {

    private final PaymentOrderJpaRepository orderJpaRepository;
    private final SubscriptionPlanJpaRepository planJpaRepository;
    private final PaymentOrderEntityMapper orderEntityMapper;

    @Override
    public PaymentOrder save(PaymentOrder paymentOrder) {
        PaymentOrderEntity entity = orderEntityMapper.domainToEntity(paymentOrder);

        SubscriptionPlanEntity planEntity = planJpaRepository.getReferenceById(paymentOrder.getSubscriptionPlanId());
        entity.setSubscriptionPlan(planEntity);

        PaymentOrderEntity savedEntity = orderJpaRepository.save(entity);
        return orderEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<PaymentOrder> findById(Long id) {
        return orderJpaRepository.findById(id)
                .map(orderEntityMapper::entityToDomain);
    }

    @Override
    public Optional<PaymentOrder> findByOrderCode(String orderCode) {
        return orderJpaRepository.findByOrderCode(orderCode)
                .map(orderEntityMapper::entityToDomain);
    }

    @Override
    public Optional<PaymentOrder> findByOrderCodeForUpdate(String orderCode) {
        return orderJpaRepository.findByOrderCodeForUpdate(orderCode)
                .map(orderEntityMapper::entityToDomain);
    }

    @Override
    public boolean existsByOrderCode(String orderCode) {
        return orderJpaRepository.existsByOrderCode(orderCode);
    }

    @Override
    public Optional<PaymentOrder> findPendingByUserId(Long userId) {
        List<PaymentOrderEntity> list = orderJpaRepository.findAllPendingByUserId(userId);
        if (list.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(orderEntityMapper.entityToDomain(list.get(0)));
    }

    @Override
    public List<PaymentOrder> findAllByUserId(Long userId) {
        return orderJpaRepository.findByUserIdOrderByCreatedTimeDesc(userId)
                .stream()
                .map(orderEntityMapper::entityToDomain)
                .toList();
    }


    @Override
    public void expirePendingBefore(Instant now) {
        orderJpaRepository.expirePendingBefore(now);
    }
}
