package org.naho.daily.port.in;

import org.naho.daily.result.UserDailyMissionResult;

import java.util.List;

public interface CrudUserDailyMissionInputPort {
    List<UserDailyMissionResult> findAllByUserId(Long userId);
}
