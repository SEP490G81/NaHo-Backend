package org.naho.daily.port.out;

import org.naho.daily.model.UserDailyMission;

public interface UserDailyMissionRepositoryPort {
    boolean existsByUserIdAndDailyMissionId(Long userId, Long dailyMissionId);

    UserDailyMission save(UserDailyMission userDailyMission);
}
