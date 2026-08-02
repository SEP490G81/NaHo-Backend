package org.naho.subscription.mapper;

import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;

public class UserSubscriptionResultMapper {

    public UserSubscriptionResult mapToUserSubscriptionResult(UserSubscription subscription, SubscriptionPlanResult planResult) {
        if (subscription == null) {
            return null;
        }
        return new UserSubscriptionResult(
                subscription.getId(),
                subscription.getUserId(),
                subscription.getSubscriptionPlanId(),
                subscription.getPaymentOrderId(),
                subscription.getStatus(),
                subscription.getStartTime(),
                subscription.getEndTime(),
                subscription.getCreatedTime(),
                subscription.getModifiedTime(),
                planResult
        );
    }
}
