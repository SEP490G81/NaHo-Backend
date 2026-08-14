package org.naho.subscription.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.subscription.command.UpdateSubscriptionPlanCommand;
import org.naho.subscription.dto.request.UpdateSubscriptionPlanRequest;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanRequestMapper {

    @Mapping(target = "planId", source = "planId")
    @Mapping(target = "adminUserId", source = "adminUserId")
    UpdateSubscriptionPlanCommand toUpdateCommand(UpdateSubscriptionPlanRequest request, Long planId, Long adminUserId);
}
