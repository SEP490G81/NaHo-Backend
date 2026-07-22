package org.naho.daily.port.out;

import org.naho.daily.model.DailyReward;

import java.util.List;

public interface DailyRewardRepositoryPort {
    List<DailyReward> findAllByRewardYearMonthOrderByDayOfMonth(String rewardYearMonth);

    boolean existsByRewardYearMonth(String rewardYearMonth);
}
