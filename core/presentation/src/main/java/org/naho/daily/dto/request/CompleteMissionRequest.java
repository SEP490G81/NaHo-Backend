package org.naho.daily.dto.request;

import org.naho.daily.type.MissionType;

public record CompleteMissionRequest(
        MissionType missionType,
        Double earnedPoint
) {
}
