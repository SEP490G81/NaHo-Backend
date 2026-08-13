package org.naho.subscription.port.in;

import org.naho.subscription.command.UpdateSubscriptionPlanCommand;
import org.naho.subscription.result.SubscriptionPlanResult;

public interface UpdateSubscriptionPlanInputPort {
    SubscriptionPlanResult updateSubscriptionPlan(UpdateSubscriptionPlanCommand command);
}
