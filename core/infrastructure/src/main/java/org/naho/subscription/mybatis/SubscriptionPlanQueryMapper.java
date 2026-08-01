package org.naho.subscription.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.naho.subscription.entity.SubscriptionPlanEntity;
import org.naho.subscription.type.SubscriptionStatus;

import java.time.Instant;
import java.util.Optional;

@Mapper
public interface SubscriptionPlanQueryMapper {
    Optional<SubscriptionPlanEntity> findCurrentSubscriptionPlanByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") SubscriptionStatus status,
            @Param("now") Instant now
    );
}
