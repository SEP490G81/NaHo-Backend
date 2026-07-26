package org.naho.subscription.usecase;

import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;
import org.naho.subscription.type.SubscriptionStatus;

import java.time.Instant;
import java.util.Optional;

public class GetActiveSubscriptionUseCase implements GetActiveSubscriptionInputPort {

    private final UserSubscriptionRepositoryPort subscriptionRepositoryPort;
    private final SubscriptionPlanRepositoryPort planRepositoryPort;
    private final SubscriptionPlanResultMapper planResultMapper;

    public GetActiveSubscriptionUseCase(UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                                        SubscriptionPlanRepositoryPort planRepositoryPort,
                                        SubscriptionPlanResultMapper planResultMapper) {
        this.subscriptionRepositoryPort = subscriptionRepositoryPort;
        this.planRepositoryPort = planRepositoryPort;
        this.planResultMapper = planResultMapper;
    }

    public GetActiveSubscriptionUseCase(UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                                        SubscriptionPlanRepositoryPort planRepositoryPort) {
        this(subscriptionRepositoryPort, planRepositoryPort, new SubscriptionPlanResultMapper());
    }

    @Override
    public UserSubscriptionResult getActiveSubscription(Long userId) {
        Instant now = Instant.now();
        Optional<UserSubscription> activeSubOpt = subscriptionRepositoryPort.findActiveByUserId(userId, now);

        if (activeSubOpt.isPresent()) {
            UserSubscription sub = activeSubOpt.get();
            SubscriptionPlanResult planResult = planRepositoryPort.findById(sub.getSubscriptionPlanId())
                    .map(planResultMapper::mapToPlanResult)
                    .orElse(null);

            return new UserSubscriptionResult(
                    sub.getId(),
                    sub.getUserId(),
                    sub.getSubscriptionPlanId(),
                    sub.getPaymentOrderId(),
                    sub.getStatus(),
                    sub.getStartTime(),
                    sub.getEndTime(),
                    sub.getCreatedTime(),
                    sub.getModifiedTime(),
                    planResult
            );
        }

        // Fallback: If user has no active paid subscription, return active FREE plan info
        SubscriptionPlan freePlan = planRepositoryPort.findActiveByCode("FREE")
                .orElse(null);

        SubscriptionPlanResult freePlanResult = planResultMapper.mapToPlanResult(freePlan);

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
}
