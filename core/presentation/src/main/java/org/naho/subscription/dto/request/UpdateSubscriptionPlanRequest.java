package org.naho.subscription.dto.request;

import jakarta.validation.constraints.Min;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;

import java.math.BigDecimal;

public record UpdateSubscriptionPlanRequest(
        String description,

        PlanTier tier,

        @Min(value = 0, message = "Price amount must be greater than or equal to 0")
        BigDecimal priceAmount,

        String priceCurrency,

        @Min(value = 1, message = "Duration days must be greater than 0")
        Integer durationDays,

        @Min(value = 0, message = "Limit must be greater than or equal to 0")
        Integer dailySpeakingQuestionEvaluationLimit,

        @Min(value = 0, message = "Recording seconds must be greater than or equal to 0")
        Integer maxSpeakingQuestionRecordingSeconds,

        @Min(value = 0, message = "Turns must be greater than or equal to 0")
        Integer maxTurnsPerAiSession,

        @Min(value = 0, message = "Limit must be greater than or equal to 0")
        Integer dailyAiSessionStartLimit,

        @Min(value = 0, message = "Speaking seconds must be greater than or equal to 0")
        Integer maxAiTurnSpeakingSeconds,

        Boolean sampleAnswerEnabled,

        PlanStatus status
) {
}
