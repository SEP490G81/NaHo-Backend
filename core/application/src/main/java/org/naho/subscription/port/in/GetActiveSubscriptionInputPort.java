package org.naho.subscription.port.in;

import org.naho.subscription.result.SubscriptionPlanResult;

public interface GetActiveSubscriptionInputPort {
    SubscriptionPlanResult getUserActiveSubscriptionPlan(Long userId);
}
