package org.naho.daily.repository;

import org.naho.daily.entity.DailyMissionEntity;
import org.naho.daily.type.MissionType;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.Optional;

public interface DailyMissionJpaRepository extends BaseJpaRepository<DailyMissionEntity> {
    boolean existsByMissionType(MissionType missionType);

    Optional<DailyMissionEntity> findByMissionType(MissionType missionType);
}
