package org.naho.daily.dto.response;

import org.naho.chest.result.ChestResult;

public record DailyRewardResponse(
        Long id,
        ChestResult chest,
        String rewardYearMonth,
        Integer dayOfMonth
) {
}
