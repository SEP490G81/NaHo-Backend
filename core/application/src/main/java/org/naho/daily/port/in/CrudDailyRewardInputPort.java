package org.naho.daily.port.in;

import org.naho.daily.command.EarnDailyRewardCommand;
import org.naho.daily.result.DailyRewardResult;
import org.naho.daily.result.UserDailyAttendanceResult;

import java.time.YearMonth;
import java.util.List;

public interface CrudDailyRewardInputPort {
    List<DailyRewardResult> getCurrentMonthDailyRewards();

    List<DailyRewardResult> createMonthlyDailyRewards(YearMonth yearMonth);

    UserDailyAttendanceResult earnDailyReward(EarnDailyRewardCommand command);
}
