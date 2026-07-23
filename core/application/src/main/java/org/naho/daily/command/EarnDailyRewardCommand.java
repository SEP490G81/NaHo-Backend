package org.naho.daily.command;

public record EarnDailyRewardCommand(
        Long userId,
        Long dailyRewardId
) {
}
