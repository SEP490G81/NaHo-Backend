package org.naho.subscription.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.subscription.entity.SubscriptionPlanEntity;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionPlanJpaRepository extends BaseJpaRepository<SubscriptionPlanEntity> {
    Optional<SubscriptionPlanEntity> findByCodeAndStatus(PlanCode code, PlanStatus status);

    List<SubscriptionPlanEntity> findAllByStatus(PlanStatus status);

    Optional<SubscriptionPlanEntity> findByCode(PlanCode code);
}
