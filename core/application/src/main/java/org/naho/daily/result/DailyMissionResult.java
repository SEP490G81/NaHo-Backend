package org.naho.daily.result;

import org.naho.daily.type.MissionType;

public record DailyMissionResult(
        Long id,
        String title,
        String description,
        Double point,
        MissionType missionType
) {
}
