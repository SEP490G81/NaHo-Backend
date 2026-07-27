package org.naho.daily.usecase;

import org.naho.daily.mapper.UserDailyMissionResultMapper;
import org.naho.daily.model.UserDailyMission;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.daily.port.out.UserDailyMissionRepositoryPort;
import org.naho.daily.result.UserDailyMissionResult;

import java.util.List;

public class CrudUserDailyMissionUseCase implements CrudUserDailyMissionInputPort {
    private final UserDailyMissionRepositoryPort userDailyMissionRepositoryPort;
    private final UserDailyMissionResultMapper userDailyMissionResultMapper;

    public CrudUserDailyMissionUseCase(
            UserDailyMissionRepositoryPort userDailyMissionRepositoryPort,
            UserDailyMissionResultMapper userDailyMissionResultMapper
    ) {
        this.userDailyMissionRepositoryPort = userDailyMissionRepositoryPort;
        this.userDailyMissionResultMapper = userDailyMissionResultMapper;
    }

    @Override
    public List<UserDailyMissionResult> findAllByUserId(Long userId) {
        List<UserDailyMission> userDailyMissions = userDailyMissionRepositoryPort.findAllByUserId(userId);
        return userDailyMissions.stream().map(userDailyMissionResultMapper::domainToResult).toList();
    }
}
