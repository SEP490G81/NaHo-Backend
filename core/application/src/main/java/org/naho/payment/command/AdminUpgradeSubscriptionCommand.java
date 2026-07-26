package org.naho.payment.command;

public record AdminUpgradeSubscriptionCommand(
        Long adminUserId,
        Long targetUserId,
        String planCode,
        Integer customDurationDays
) {
}
