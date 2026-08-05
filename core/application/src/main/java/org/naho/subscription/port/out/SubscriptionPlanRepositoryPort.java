package org.naho.subscription.port.out;

import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.SubscriptionStatus;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepositoryPort {
    Optional<SubscriptionPlan> findById(Long id);

    Optional<SubscriptionPlan> findActiveByCode(PlanCode code);

    List<SubscriptionPlan> findAllActive();

    Optional<SubscriptionPlan> findCurrentSubscriptionPlanByUserIdAndStatus(
            Long userId,
            SubscriptionStatus status,
            Instant now
    );

    Optional<SubscriptionPlan> findByCode(PlanCode code);
}
