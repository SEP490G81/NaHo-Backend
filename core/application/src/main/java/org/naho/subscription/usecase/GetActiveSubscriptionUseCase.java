package org.naho.subscription.usecase;

import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.mapper.UserSubscriptionResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.SubscriptionStatus;

import java.time.Instant;
import java.util.Optional;

public class GetActiveSubscriptionUseCase implements GetActiveSubscriptionInputPort {

    private final UserSubscriptionRepositoryPort userSubscriptionRepositoryPort;
    private final SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort;
    private final SubscriptionPlanResultMapper subscriptionPlanResultMapper;
    private final UserSubscriptionResultMapper userSubscriptionResultMapper;

    public GetActiveSubscriptionUseCase(
            UserSubscriptionRepositoryPort userSubscriptionRepositoryPort,
            SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort,
            SubscriptionPlanResultMapper subscriptionPlanResultMapper,
            UserSubscriptionResultMapper userSubscriptionResultMapper
    ) {
        this.userSubscriptionRepositoryPort = userSubscriptionRepositoryPort;
        this.subscriptionPlanRepositoryPort = subscriptionPlanRepositoryPort;
        this.subscriptionPlanResultMapper = subscriptionPlanResultMapper;
        this.userSubscriptionResultMapper = userSubscriptionResultMapper;
    }

    @Override
    public UserSubscriptionResult getActiveSubscription(Long userId) {
        Instant now = Instant.now();
        Optional<UserSubscription> activeSubOpt = userSubscriptionRepositoryPort.findActiveByUserId(userId, now);

        if (activeSubOpt.isPresent()) {
            UserSubscription sub = activeSubOpt.get();
            SubscriptionPlanResult planResult = subscriptionPlanRepositoryPort.findById(sub.getSubscriptionPlanId())
                    .map(subscriptionPlanResultMapper::mapToPlanResult)
                    .orElse(null);

            return userSubscriptionResultMapper.mapToUserSubscriptionResult(sub, planResult);
        }

        // Fallback: If user has no active paid subscription, return active FREE plan info
        SubscriptionPlan freePlan = subscriptionPlanRepositoryPort.findActiveByCode(PlanCode.FREE)
                .orElse(null);

        SubscriptionPlanResult freePlanResult = subscriptionPlanResultMapper.mapToPlanResult(freePlan);

        return new UserSubscriptionResult(
                null,
                userId,
                freePlan != null ? freePlan.getId() : null,
                null,
                SubscriptionStatus.ACTIVE,
                null,
                null,
                null,
                null,
                freePlanResult
        );
    }

    @Override
    public SubscriptionPlanResult getUserActiveSubscriptionPlan(Long userId) {
        Instant now = Instant.now();

        Optional<SubscriptionPlan> subscriptionPlan = subscriptionPlanRepositoryPort
                .findCurrentSubscriptionPlanByUserIdAndStatus(
                        userId,
                        SubscriptionStatus.ACTIVE,
                        now
                );

        if (subscriptionPlan.isPresent()) {
            return subscriptionPlanResultMapper.mapToPlanResult(subscriptionPlan.get());
        }
        return subscriptionPlanRepositoryPort.findByCode(PlanCode.FREE)
                .map(subscriptionPlanResultMapper::mapToPlanResult)
                .orElseThrow(() -> new ApplicationException(
                        SubscriptionErrorCode.PLAN_NOT_FOUND,
                        SubscriptionDetailMessageKey.PLAN_NOT_FOUND
                ));
    }
}
