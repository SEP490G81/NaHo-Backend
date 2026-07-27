package org.naho.daily.result;

import java.time.Instant;

public record UserDailyMissionResult(
        Long id,
        Long userId,
        Long dailyMissionId,
        Instant completedAt
) {
}
