package org.naho.subscription.port.in;

import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;

public interface GetActiveSubscriptionInputPort {
    SubscriptionPlanResult getUserActiveSubscriptionPlan(Long userId);

    UserSubscriptionResult getUserActiveSubscription(Long userId);
}
