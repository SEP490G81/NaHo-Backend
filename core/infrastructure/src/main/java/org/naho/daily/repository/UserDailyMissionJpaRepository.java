package org.naho.daily.repository;

import org.naho.daily.entity.UserDailyMissionEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.List;

public interface UserDailyMissionJpaRepository extends BaseJpaRepository<UserDailyMissionEntity> {
    boolean existsByUser_IdAndDailyMission_Id(Long userId, Long dailyMissionId);

    List<UserDailyMissionEntity> findAllByUser_Id(Long userId);
}
