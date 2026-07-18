package org.naho.subscription.usecase;

import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.in.ListActivePlansInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;

import java.util.List;

public class ListActivePlansUseCase implements ListActivePlansInputPort {

    private final SubscriptionPlanRepositoryPort planRepositoryPort;

    public ListActivePlansUseCase(SubscriptionPlanRepositoryPort planRepositoryPort) {
        this.planRepositoryPort = planRepositoryPort;
    }

    @Override
    public List<SubscriptionPlanResult> listActivePlans() {
        List<SubscriptionPlan> plans = planRepositoryPort.findAllActive();
        return plans.stream()
                .map(plan -> new SubscriptionPlanResult(
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
                        plan.getModifiedTime()))
                .toList();
    }
}
