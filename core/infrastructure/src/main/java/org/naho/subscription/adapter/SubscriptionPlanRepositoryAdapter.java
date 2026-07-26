package org.naho.subscription.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.subscription.mapper.SubscriptionPlanEntityMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.repository.SubscriptionPlanJpaRepository;
import org.naho.subscription.type.PlanStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanRepositoryAdapter implements SubscriptionPlanRepositoryPort {

    private final SubscriptionPlanJpaRepository planJpaRepository;
    private final SubscriptionPlanEntityMapper planEntityMapper;

    @Override
    public Optional<SubscriptionPlan> findById(Long id) {
        return planJpaRepository.findById(id)
                .map(planEntityMapper::entityToDomain);
    }

    @Override
    public Optional<SubscriptionPlan> findActiveByCode(String code) {
        return planJpaRepository.findByCodeAndStatus(code, PlanStatus.ACTIVE)
                .map(planEntityMapper::entityToDomain);
    }

    @Override
    public List<SubscriptionPlan> findAllActive() {
        return planJpaRepository.findAllByStatus(PlanStatus.ACTIVE).stream()
                .map(planEntityMapper::entityToDomain)
                .toList();
    }
}
