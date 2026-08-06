package org.naho.daily.port.out;

import org.naho.daily.model.DailyMission;

import java.util.List;
import java.util.Optional;

public interface DailyMissionRepositoryPort {
    List<DailyMission> saveAll(List<DailyMission> dailyMissions);

    DailyMission save(DailyMission dailyMission);

    Optional<DailyMission> findById(Long id);

    List<DailyMission> findAll();
}
