package org.naho.daily.port.in;

import org.naho.daily.command.CompleteMissionCommand;
import org.naho.daily.result.DailyMissionResult;

import java.util.List;

public interface CrudDailyMissionInputPort {
    void createTodayMissions();

    void completeMission(CompleteMissionCommand command);

    List<DailyMissionResult> getTodayMissions();
}
