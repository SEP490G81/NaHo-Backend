package org.naho.daily.dto.response;

import org.naho.chest.dto.response.ChestResponse;

public record DailyRewardResponse(
        Long id,
        ChestResponse chest,
        String rewardYearMonth,
        Integer dayOfMonth
) {
}
