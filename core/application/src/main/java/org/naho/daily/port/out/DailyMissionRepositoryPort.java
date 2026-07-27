package org.naho.daily.port.out;

import org.naho.daily.model.DailyMission;
import org.naho.daily.type.MissionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyMissionRepositoryPort {
    List<DailyMission> saveAll(List<DailyMission> dailyMissions);

    DailyMission save(DailyMission dailyMission);

    boolean existsByMissionDate(LocalDate missionDate);

    Optional<DailyMission> findByMissionDateAndMissionType(LocalDate missionDate, MissionType missionType);

    List<DailyMission> findAllByMissionDate(LocalDate missionDate);
}
