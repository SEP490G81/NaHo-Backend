package org.naho.subscription.dto.response;

import java.time.LocalDate;

public record UserDailyAiUsageResponse(
        Long id,
        Long userId,
        LocalDate usageDate,
        Integer speakingEvaluationCount,
        Integer aiSessionStartCount
) {
}
