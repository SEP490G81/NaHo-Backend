package org.naho.daily.port.out;

import org.naho.daily.model.UserDailyMission;
import org.naho.daily.type.MissionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserDailyMissionRepositoryPort {
    UserDailyMission save(UserDailyMission userDailyMission);

    List<UserDailyMission> findAllByUserId(Long userId);

    List<UserDailyMission> findAllByUser_IdAndStartedDate(Long userId, LocalDate startedDate);

    Optional<UserDailyMission> findById(Long id);

    Optional<UserDailyMission> findByIdAndUserId(Long id, Long userId);

    Optional<UserDailyMission> findByUserIdAndDailyMissionMissionTypeAndStartedDate(Long userId, MissionType missionType, LocalDate startedDate);
}
