package org.naho.subscription.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.subscription.entity.UserSubscriptionEntity;
import org.naho.subscription.type.SubscriptionStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface UserSubscriptionJpaRepository extends BaseJpaRepository<UserSubscriptionEntity> {

    @Query("SELECT us FROM UserSubscriptionEntity us WHERE us.user.id = :userId AND us.status = :status AND :now >= us.startTime AND :now < us.endTime ORDER BY us.endTime DESC")
    List<UserSubscriptionEntity> findActiveSubscriptions(
            @Param("userId") Long userId,
            @Param("status") SubscriptionStatus status,
            @Param("now") Instant now
    );

    @Modifying
    @Query("UPDATE UserSubscriptionEntity us SET us.status = org.naho.subscription.type.SubscriptionStatus.EXPIRED, us.modifiedTime = :now WHERE us.status = org.naho.subscription.type.SubscriptionStatus.ACTIVE AND us.endTime <= :now")
    void updateExpiredSubscriptions(@Param("now") Instant now);

    boolean existsByPaymentOrderId(Long paymentOrderId);
}
