package org.naho.daily.mapper;

import org.naho.daily.model.DailyMission;
import org.naho.daily.result.DailyMissionResult;

public class DailyMissionResultMapper {
    public DailyMissionResult domainToResult(DailyMission domain) {
        if (domain == null) return null;
        return new DailyMissionResult(
                domain.getId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getPoint(),
                domain.getMissionType()
        );
    }
}
