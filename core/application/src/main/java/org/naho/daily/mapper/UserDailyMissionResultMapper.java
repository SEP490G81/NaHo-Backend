package org.naho.daily.mapper;

import org.naho.daily.model.UserDailyMission;
import org.naho.daily.result.UserDailyMissionResult;

public class UserDailyMissionResultMapper {
    public UserDailyMissionResult domainToResult(UserDailyMission domain) {
        return new UserDailyMissionResult(
                domain.getId(),
                domain.getUserId(),
                domain.getDailyMissionId(),
                domain.getStatus(),
                domain.getCompletedAt(),
                domain.getEarnedAt()
        );
    }
}
