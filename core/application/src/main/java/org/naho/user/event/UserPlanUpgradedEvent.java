package org.naho.user.event;

public record UserPlanUpgradedEvent(
    Long userId,
    String newPlanName
) {
}
