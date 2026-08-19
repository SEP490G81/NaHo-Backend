package org.naho.subscription.dto.response;

import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;

import java.math.BigDecimal;

public record SubscriptionPlanResponse(
        Long id,
        PlanCode code,
        String description,
        PlanTier tier,
        BigDecimal priceAmount,
        String priceCurrency,
        Integer durationDays,
        Integer dailySpeakingQuestionEvaluationLimit,
        Integer maxSpeakingQuestionRecordingSeconds,
        Integer maxTurnsPerAiSession,
        Integer dailyAiSessionStartLimit,
        Integer maxAiTurnSpeakingSeconds,
        Integer maxInProgressSessionCount,
        Boolean sampleAnswerEnabled,
        PlanStatus status
) {
}
