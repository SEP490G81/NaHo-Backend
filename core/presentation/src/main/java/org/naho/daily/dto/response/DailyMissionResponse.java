package org.naho.daily.dto.response;

import org.naho.daily.type.MissionType;

public record DailyMissionResponse(
        Long id,
        String title,
        String description,
        Double point,
        MissionType missionType
) {
}
