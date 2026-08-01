package org.naho.payment.command;

import org.naho.subscription.type.PlanCode;

public record AdminUpgradeSubscriptionCommand(
        Long adminUserId,
        Long targetUserId,
        PlanCode planCode,
        Integer customDurationDays
) {
}
