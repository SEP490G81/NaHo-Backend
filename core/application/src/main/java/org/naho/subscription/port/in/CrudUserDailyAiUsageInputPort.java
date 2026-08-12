package org.naho.subscription.port.in;

import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.result.UserDailyAiUsageResult;

import java.time.LocalDate;

public interface CrudUserDailyAiUsageInputPort {
    UserDailyAiUsage findByUserIdAndUsageDate(Long userId, LocalDate usageDate);

    UserDailyAiUsageResult findTodayUserDailyAiUsage(Long userId);
}
