package org.naho.daily.repository;

import org.naho.daily.entity.UserDailyMissionEntity;
import org.naho.daily.type.MissionType;
import org.naho.shared.persistence.BaseJpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserDailyMissionJpaRepository extends BaseJpaRepository<UserDailyMissionEntity> {
    boolean existsByUser_IdAndDailyMission_Id(Long userId, Long dailyMissionId);

    List<UserDailyMissionEntity> findAllByUser_Id(Long userId);

    Optional<UserDailyMissionEntity> findByUser_IdAndDailyMission_Id(Long userId, Long dailyMissionId);

    Optional<UserDailyMissionEntity> findByUser_IdAndDailyMission_IdAndStartedDate(Long userId, Long dailyMissionId, LocalDate startedDate);

    List<UserDailyMissionEntity> findAllByUser_IdAndStartedDate(Long userId, LocalDate startedDate);

    Optional<UserDailyMissionEntity> findByIdAndUser_Id(Long id, Long userId);

    Optional<UserDailyMissionEntity> findByUser_IdAndDailyMission_MissionTypeAndStartedDate(Long userId, MissionType missionType, LocalDate startedDate);
}
