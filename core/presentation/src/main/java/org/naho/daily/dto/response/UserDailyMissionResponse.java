package org.naho.daily.dto.response;

import org.naho.daily.type.MissionStatus;

import java.time.Instant;

public record UserDailyMissionResponse(
        Long id,
        Long userId,
        Long dailyMissionId,
        MissionStatus status,
        Instant completedAt,
        Instant earnedAt
) {
}
