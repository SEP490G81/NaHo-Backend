package org.naho.subscription.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.subscription.dto.response.SubscriptionPlanResponse;
import org.naho.subscription.dto.response.UserSubscriptionResponse;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SubscriptionResponseMapper {

    SubscriptionPlanResponse planResultToResponse(SubscriptionPlanResult result);

    List<SubscriptionPlanResponse> listPlanResultToResponse(List<SubscriptionPlanResult> results);

    @Mapping(target = "plan", source = "planResult")
    UserSubscriptionResponse userSubResultToResponse(UserSubscriptionResult result);
}

