package org.naho.subscription.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.subscription.dto.response.UserDailyAiUsageResponse;
import org.naho.subscription.result.UserDailyAiUsageResult;

@Mapper(componentModel = "spring")
public interface UserDailyAiUsageResponseMapper {
    UserDailyAiUsageResponse resultToResponse(UserDailyAiUsageResult result);
}
