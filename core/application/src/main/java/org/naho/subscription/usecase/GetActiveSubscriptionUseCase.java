package org.naho.subscription.usecase;

import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.result.UserSubscriptionResult;

import java.time.Instant;

public class GetActiveSubscriptionUseCase implements GetActiveSubscriptionInputPort {

    private final UserSubscriptionRepositoryPort subscriptionRepositoryPort;

    public GetActiveSubscriptionUseCase(UserSubscriptionRepositoryPort subscriptionRepositoryPort) {
        this.subscriptionRepositoryPort = subscriptionRepositoryPort;
    }

    @Override
    public UserSubscriptionResult getActiveSubscription(Long userId) {
        return subscriptionRepositoryPort.findActiveByUserId(userId, Instant.now())
                .map(sub -> new UserSubscriptionResult(
                        sub.getId(),
                        sub.getUserId(),
                        sub.getSubscriptionPlanId(),
                        sub.getPaymentOrderId(),
                        sub.getStatus(),
                        sub.getStartTime(),
                        sub.getEndTime(),
                        sub.getCreatedTime(),
                        sub.getModifiedTime()))
                .orElse(null);
    }
}
