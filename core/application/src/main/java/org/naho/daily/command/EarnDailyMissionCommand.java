package org.naho.daily.command;

public record EarnDailyMissionCommand(
        Long userDailyMissionId,
        Long userId
) {
}
