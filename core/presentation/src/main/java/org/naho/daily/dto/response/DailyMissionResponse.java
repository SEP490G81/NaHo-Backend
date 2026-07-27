package org.naho.daily.dto.response;

import org.naho.daily.type.MissionType;

import java.time.LocalDate;

public record DailyMissionResponse(
        Long id,
        Double point,
        LocalDate missionDate,
        MissionType missionType
) {
}
