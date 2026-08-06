package org.naho.daily.mapper;

import org.naho.daily.model.DailyMission;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.port.in.CrudDailyMissionInputPort;
import org.naho.daily.result.DailyMissionResult;
import org.naho.daily.result.UserDailyMissionResult;

public class UserDailyMissionResultMapper {
    private final CrudDailyMissionInputPort crudDailyMissionInputPort;
    private final DailyMissionResultMapper dailyMissionResultMapper;

    public UserDailyMissionResultMapper(
            CrudDailyMissionInputPort crudDailyMissionInputPort,
            DailyMissionResultMapper dailyMissionResultMapper
    ) {
        this.crudDailyMissionInputPort = crudDailyMissionInputPort;
        this.dailyMissionResultMapper = dailyMissionResultMapper;
    }

    public UserDailyMissionResult domainToResult(UserDailyMission domain) {
        if (domain == null) return null;

        DailyMissionResult dailyMissionResult = crudDailyMissionInputPort
                .findById(domain.getDailyMissionId())
                .orElse(null);

        return new UserDailyMissionResult(
                domain.getId(),
                domain.getUserId(),
                dailyMissionResult,
                domain.getStatus(),
                domain.getStartedDate(),
                domain.getCompletedDate(),
                domain.getEarnedDate()
        );
    }

    public UserDailyMissionResult domainToResult(UserDailyMission domain, DailyMission dailyMission) {
        if (domain == null) return null;

        DailyMissionResult dailyMissionResult = dailyMissionResultMapper
                .domainToResult(dailyMission);

        return new UserDailyMissionResult(
                domain.getId(),
                domain.getUserId(),
                dailyMissionResult,
                domain.getStatus(),
                domain.getStartedDate(),
                domain.getCompletedDate(),
                domain.getEarnedDate()
        );
    }
}
