package org.naho.subscription.command;

import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;

import java.math.BigDecimal;

public record UpdateSubscriptionPlanCommand(
        Long planId,
        Long adminUserId,
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
