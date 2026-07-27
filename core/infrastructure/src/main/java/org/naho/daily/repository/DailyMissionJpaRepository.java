package org.naho.daily.repository;

import org.naho.daily.entity.DailyMissionEntity;
import org.naho.daily.type.MissionType;
import org.naho.shared.persistence.BaseJpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMissionJpaRepository extends BaseJpaRepository<DailyMissionEntity> {
    boolean existsByMissionDate(LocalDate missionDate);

    Optional<DailyMissionEntity> findByMissionDateAndMissionType(LocalDate missionDate, MissionType missionType);

    List<DailyMissionEntity> findAllByMissionDate(LocalDate missionDate);
}
