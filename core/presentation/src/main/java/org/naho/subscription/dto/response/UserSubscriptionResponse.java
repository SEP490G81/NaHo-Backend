package org.naho.subscription.dto.response;

import java.time.Instant;


public record UserSubscriptionResponse(
        Long id,
        Long userId,
        Long subscriptionPlanId,
        Long paymentOrderId,
        String status,
        Instant startTime,
        Instant endTime,
        SubscriptionPlanResponse subscriptionPlan
) {
}
