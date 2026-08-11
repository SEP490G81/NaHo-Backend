package org.naho.subscription.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.subscription.dto.response.UserSubscriptionResponse;
import org.naho.subscription.result.UserSubscriptionResult;

@Mapper(componentModel = "spring", uses = SubscriptionResponseMapper.class)
public interface UserSubscriptionResponseMapper {
    UserSubscriptionResponse resultToResponse(UserSubscriptionResult result);
}
