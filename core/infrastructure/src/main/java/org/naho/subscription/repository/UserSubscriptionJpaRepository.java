package org.naho.subscription.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.subscription.entity.UserSubscriptionEntity;
import org.naho.subscription.type.SubscriptionStatus;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface UserSubscriptionJpaRepository extends BaseJpaRepository<UserSubscriptionEntity> {

    @Query("SELECT us FROM UserSubscriptionEntity us WHERE us.userId = :userId AND us.status = :status AND :now >= us.startTime AND :now < us.endTime")
    Optional<UserSubscriptionEntity> findActiveSubscription(
            @Param("userId") Long userId,
            @Param("status") SubscriptionStatus status,
            @Param("now") Instant now
    );

    boolean existsByPaymentOrderId(Long paymentOrderId);
}
