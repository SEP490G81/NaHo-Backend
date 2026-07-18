package org.naho.subscription.port.out;

import org.naho.subscription.model.SubscriptionPlan;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPlanRepositoryPort {
    Optional<SubscriptionPlan> findById(Long id);

    Optional<SubscriptionPlan> findActiveByCode(String code);

    List<SubscriptionPlan> findAllActive();
}
