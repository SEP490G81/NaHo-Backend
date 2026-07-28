package org.naho.daily.port.in;

import org.naho.daily.result.DailyMissionResult;

import java.util.Optional;

public interface CrudDailyMissionInputPort {
    Optional<DailyMissionResult> findById(Long id);
}
