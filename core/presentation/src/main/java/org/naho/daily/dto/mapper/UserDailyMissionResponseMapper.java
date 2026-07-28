package org.naho.daily.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.daily.dto.response.UserDailyMissionResponse;
import org.naho.daily.result.UserDailyMissionResult;

@Mapper(componentModel = "spring", uses = {DailyMissionResponseMapper.class})
public interface UserDailyMissionResponseMapper {
    UserDailyMissionResponse resultToResponse(UserDailyMissionResult result);
}
