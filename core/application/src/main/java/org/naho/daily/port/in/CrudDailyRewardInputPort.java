package org.naho.daily.port.in;

import org.naho.daily.result.DailyRewardResult;

import java.util.List;

public interface CrudDailyRewardInputPort {
    List<DailyRewardResult> getCurrentMonthDailyRewards();

    List<DailyRewardResult> createCurrentMonthDailyRewards();
}
