package org.naho.subscription.port.out;

import org.naho.subscription.model.UserSubscription;

import java.time.Instant;
import java.util.Optional;

public interface UserSubscriptionRepositoryPort {
    UserSubscription save(UserSubscription subscription);

    Optional<UserSubscription> findActiveByUserId(Long userId, Instant now);

    boolean existsByPaymentOrderId(Long paymentOrderId);
}
