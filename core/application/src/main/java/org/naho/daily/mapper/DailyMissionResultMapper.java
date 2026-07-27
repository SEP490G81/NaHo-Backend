package org.naho.daily.mapper;

import org.naho.daily.model.DailyMission;
import org.naho.daily.result.DailyMissionResult;

public class DailyMissionResultMapper {
    public DailyMissionResult domainToResult(DailyMission domain) {
        return new DailyMissionResult(
                domain.getId(),
                domain.getPoint(),
                domain.getMissionDate(),
                domain.getMissionType()
        );
    }
}
