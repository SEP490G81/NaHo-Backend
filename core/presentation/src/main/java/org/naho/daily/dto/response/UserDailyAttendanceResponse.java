package org.naho.daily.dto.response;

import java.time.LocalDate;

public record UserDailyAttendanceResponse(
        Long id,
        Long userId,
        Long dailyRewardId,
        LocalDate attendanceDate,
        Integer earnedPoint
) {
}
