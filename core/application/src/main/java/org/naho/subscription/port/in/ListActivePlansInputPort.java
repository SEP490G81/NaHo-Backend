package org.naho.subscription.port.in;

import org.naho.subscription.result.SubscriptionPlanResult;

import java.util.List;

public interface ListActivePlansInputPort {
    List<SubscriptionPlanResult> listActivePlans();
}
