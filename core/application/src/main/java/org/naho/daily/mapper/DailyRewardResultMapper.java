package org.naho.daily.mapper;

import org.naho.chest.result.ChestResult;
import org.naho.daily.model.DailyReward;
import org.naho.daily.result.DailyRewardResult;

public class DailyRewardResultMapper {

    public DailyRewardResult domainToResult(DailyReward domain, ChestResult chestResult) {
        return DailyRewardResult.builder()
                .id(domain.getId())
                .chest(chestResult)
                .rewardYearMonth(domain.getRewardYearMonth().getValue())
                .dayOfMonth(domain.getDayOfMonth())
                .build();
    }
}
