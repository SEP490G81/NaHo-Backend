package org.naho.daily.result;

import org.naho.daily.type.MissionStatus;

import java.time.LocalDate;

public record UserDailyMissionResult(
        Long id,
        Long userId,
        DailyMissionResult dailyMission,
        MissionStatus status,
        LocalDate startedDate,
        LocalDate completedDate,
        LocalDate earnedDate
) {
}
