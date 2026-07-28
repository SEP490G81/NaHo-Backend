package org.naho.daily.dto.response;

import org.naho.daily.type.MissionStatus;

import java.time.LocalDate;

public record UserDailyMissionResponse(
        Long id,
        Long userId,
        DailyMissionResponse dailyMission,
        MissionStatus status,
        LocalDate startedDate,
        LocalDate completedDate,
        LocalDate earnedDate
) {
}
