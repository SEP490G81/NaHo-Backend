package org.naho.subscription.result;

import org.naho.subscription.type.SubscriptionStatus;

import java.time.Instant;

public record UserSubscriptionResult(
        Long id,
        Long userId,
        Long subscriptionPlanId,
        Long paymentOrderId,
        SubscriptionStatus status,
        Instant startTime,
        Instant endTime,
        Instant createdTime,
        Instant modifiedTime,
        SubscriptionPlanResult planResult) {
}
