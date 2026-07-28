package org.naho.daily.result;

import org.naho.daily.type.MissionStatus;

import java.time.Instant;

public record UserDailyMissionResult(
        Long id,
        Long userId,
        Long dailyMissionId,
        MissionStatus status,
        Instant completedAt,
        Instant earnedAt
) {
}
