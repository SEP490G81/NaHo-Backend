package org.naho.subscription.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.subscription.mapper.SubscriptionPlanEntityMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.mybatis.SubscriptionPlanQueryMapper;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.repository.SubscriptionPlanJpaRepository;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.SubscriptionStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SubscriptionPlanRepositoryAdapter implements SubscriptionPlanRepositoryPort {

    private final SubscriptionPlanJpaRepository subscriptionPlanJpaRepository;
    private final SubscriptionPlanEntityMapper subscriptionPlanEntityMapper;
    private final SubscriptionPlanQueryMapper subscriptionPlanQueryMapper;

    @Override
    public Optional<SubscriptionPlan> findById(Long id) {
        return subscriptionPlanJpaRepository.findById(id)
                .map(subscriptionPlanEntityMapper::entityToDomain);
    }

    @Override
    public Optional<SubscriptionPlan> findActiveByCode(PlanCode code) {
        return subscriptionPlanJpaRepository.findByCodeAndStatus(code, PlanStatus.ACTIVE)
                .map(subscriptionPlanEntityMapper::entityToDomain);
    }

    @Override
    public List<SubscriptionPlan> findAllActive() {
        return subscriptionPlanJpaRepository.findAllByStatus(PlanStatus.ACTIVE).stream()
                .map(subscriptionPlanEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Optional<SubscriptionPlan> findCurrentSubscriptionPlanByUserIdAndStatus(Long userId, SubscriptionStatus status, Instant now) {
        return subscriptionPlanQueryMapper
                .findCurrentSubscriptionPlanByUserIdAndStatus(userId, status, now)
                .map(subscriptionPlanEntityMapper::entityToDomain);
    }

    @Override
    public Optional<SubscriptionPlan> findByCode(PlanCode code) {
        return subscriptionPlanJpaRepository
                .findByCode(code)
                .map(subscriptionPlanEntityMapper::entityToDomain);
    }
}
