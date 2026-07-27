package org.naho.daily.dto.response;

import java.time.Instant;

public record UserDailyMissionResponse(
        Long id,
        Long userId,
        Long dailyMissionId,
        Instant completedAt
) {
}
