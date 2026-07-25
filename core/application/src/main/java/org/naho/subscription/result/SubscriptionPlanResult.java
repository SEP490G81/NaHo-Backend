package org.naho.subscription.result;

import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;

import java.math.BigDecimal;
import java.time.Instant;

public record SubscriptionPlanResult(
        Long id,
        String code,
        String name,
        String description,
        PlanTier tier,
        BigDecimal priceAmount,
        String priceCurrency,
        int durationDays,
        int monthlyAssessmentLimit,
        long monthlyAssessmentAudioSeconds,
        int maxAssessmentAudioSeconds,
        long monthlyConversationSeconds,
        int maxConversationSessionSeconds,
        int maxConversationTurnsPerSession,
        boolean fullCurriculumAccess,
        boolean progressAnalyticsEnabled,
        boolean sampleAnswerEnabled,
        PlanStatus status,
        Instant createdTime,
        Instant modifiedTime) {
}
