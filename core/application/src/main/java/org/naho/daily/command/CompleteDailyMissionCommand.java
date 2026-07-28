package org.naho.daily.command;

import org.naho.daily.type.MissionType;

public record CompleteDailyMissionCommand(
        Long userId,
        MissionType missionType
) {
}
