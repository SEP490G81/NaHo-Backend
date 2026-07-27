package org.naho.daily.result;

import org.naho.daily.type.MissionType;

import java.time.LocalDate;

public record DailyMissionResult(
        Long id,
        Double point,
        LocalDate missionDate,
        MissionType missionType
) {
}
