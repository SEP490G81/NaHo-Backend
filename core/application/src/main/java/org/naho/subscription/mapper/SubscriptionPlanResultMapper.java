package org.naho.subscription.mapper;

import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.result.SubscriptionPlanResult;

public class SubscriptionPlanResultMapper {

    public SubscriptionPlanResult mapToPlanResult(SubscriptionPlan plan) {
        if (plan == null) {
            return null;
        }
        return new SubscriptionPlanResult(
                plan.getId(),
                plan.getCode(),
                plan.getName(),
                plan.getDescription(),
                plan.getTier(),
                plan.getPrice().amount(),
                plan.getPrice().currency().getCurrencyCode(),
                plan.getDurationDays(),
                plan.getQuota().monthlyAssessmentLimit(),
                plan.getQuota().monthlyAssessmentAudioSeconds(),
                plan.getQuota().maxAssessmentAudioSeconds(),
                plan.getQuota().monthlyConversationSeconds(),
                plan.getQuota().maxConversationSessionSeconds(),
                plan.getQuota().maxConversationTurnsPerSession(),
                plan.isFullCurriculumAccess(),
                plan.isProgressAnalyticsEnabled(),
                plan.isSampleAnswerEnabled(),
                plan.getStatus(),
                plan.getCreatedTime(),
                plan.getModifiedTime());
    }
}
