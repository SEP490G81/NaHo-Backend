package org.naho.daily.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.daily.dto.response.UserDailyAttendanceResponse;
import org.naho.daily.result.UserDailyAttendanceResult;

@Mapper(componentModel = "spring")
public interface UserDailyAttendanceResponseMapper {
    UserDailyAttendanceResponse resultToResponse(UserDailyAttendanceResult result);
}
