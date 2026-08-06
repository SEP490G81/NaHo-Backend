package org.naho.subscription.result;

import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;

import java.math.BigDecimal;

public record SubscriptionPlanResult(
        Long id,
        PlanCode code,
        String name,
        String description,
        PlanTier tier,
        BigDecimal priceAmount,
        String priceCurrency,
        Integer durationDays,
        int monthlyAssessmentLimit,
        long monthlyAssessmentAudioSeconds,
        int maxAssessmentAudioSeconds,
        long monthlyConversationSeconds,
        int maxConversationSessionSeconds,
        int maxConversationTurnsPerSession,
        Boolean fullCurriculumAccess,
        Boolean progressAnalyticsEnabled,
        Boolean sampleAnswerEnabled,
        Double maxAnswerTimeSeconds,
        Boolean saveAnswerHistoryEnabled,
        PlanStatus status
) {
}
