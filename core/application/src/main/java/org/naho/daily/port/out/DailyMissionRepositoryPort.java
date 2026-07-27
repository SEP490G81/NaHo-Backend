package org.naho.daily.port.out;

import org.naho.daily.model.DailyMission;

import java.time.LocalDate;
import java.util.List;

public interface DailyMissionRepositoryPort {
    List<DailyMission> saveAll(List<DailyMission> dailyMissions);

    boolean existsByMissionDate(LocalDate missionDate);
}
