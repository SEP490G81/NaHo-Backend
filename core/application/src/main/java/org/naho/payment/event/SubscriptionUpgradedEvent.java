package org.naho.payment.event;

public record SubscriptionUpgradedEvent(
        Long targetUserId,
        String planName
) {
}
