package org.naho.daily.port.in;

import org.naho.daily.command.CompleteDailyMissionCommand;
import org.naho.daily.command.EarnDailyMissionCommand;
import org.naho.daily.result.UserDailyMissionResult;

import java.util.List;

public interface CrudUserDailyMissionInputPort {
    List<UserDailyMissionResult> findAllUserTodayMissions(Long userId);

    UserDailyMissionResult completeMission(CompleteDailyMissionCommand command);

    UserDailyMissionResult earnMission(EarnDailyMissionCommand command);
}
