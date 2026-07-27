package org.naho.daily.repository;

import org.naho.daily.entity.DailyMissionEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.time.LocalDate;

public interface DailyMissionJpaRepository extends BaseJpaRepository<DailyMissionEntity> {
    boolean existsByMissionDate(LocalDate missionDate);
}
