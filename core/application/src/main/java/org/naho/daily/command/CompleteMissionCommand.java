package org.naho.daily.command;

import org.naho.daily.type.MissionType;

public record CompleteMissionCommand(
        MissionType missionType,
        Long userId,
        Double earnedPoint
) {
}
