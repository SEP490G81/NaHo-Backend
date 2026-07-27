package org.naho.daily.port.out;

import org.naho.daily.model.UserDailyMission;

import java.util.List;

public interface UserDailyMissionRepositoryPort {
    boolean existsByUserIdAndDailyMissionId(Long userId, Long dailyMissionId);

    UserDailyMission save(UserDailyMission userDailyMission);

    List<UserDailyMission> findAllByUserId(Long userId);
}
