package org.naho.daily.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.daily.dto.response.DailyMissionResponse;
import org.naho.daily.result.DailyMissionResult;

@Mapper(componentModel = "spring")
public interface DailyMissionResponseMapper {
    DailyMissionResponse resultToResponse(DailyMissionResult result);
}
