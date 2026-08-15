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
                plan.getDescription(),
                plan.getTier(),
                plan.getPrice().amount(),
                plan.getPrice().currency().getCurrencyCode(),
                plan.getDurationDays(),
                plan.getDailySpeakingQuestionEvaluationLimit(),
                plan.getMaxSpeakingQuestionRecordingSeconds(),
                plan.getMaxTurnsPerAiSession(),
                plan.getDailyAiSessionStartLimit(),
                plan.getMaxAiTurnSpeakingSeconds(),
                plan.isSampleAnswerEnabled(),
                plan.getStatus());
    }
}
