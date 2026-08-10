package org.naho.subscription.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.subscription.dto.response.SubscriptionPlanResponse;
import org.naho.subscription.result.SubscriptionPlanResult;

@Mapper(componentModel = "spring")
public interface SubscriptionResponseMapper {
    SubscriptionPlanResponse resultToResponse(SubscriptionPlanResult result);
}

