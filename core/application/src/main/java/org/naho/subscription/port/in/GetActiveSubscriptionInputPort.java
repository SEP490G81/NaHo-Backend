package org.naho.subscription.port.in;

import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;

public interface GetActiveSubscriptionInputPort {
    UserSubscriptionResult getActiveSubscription(Long userId);

    SubscriptionPlanResult getUserActiveSubscriptionPlan(Long userId);
}
