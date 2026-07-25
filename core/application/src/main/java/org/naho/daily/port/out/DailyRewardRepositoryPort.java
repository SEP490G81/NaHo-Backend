package org.naho.daily.port.out;

import org.naho.daily.model.DailyReward;

import java.util.List;
import java.util.Optional;

public interface DailyRewardRepositoryPort {
    List<DailyReward> findAllByRewardYearMonthOrderByDayOfMonth(String rewardYearMonth);

    boolean existsByRewardYearMonth(String rewardYearMonth);

    List<DailyReward> saveAll(List<DailyReward> dailyRewards);

    Optional<DailyReward> findById(Long id);
}
