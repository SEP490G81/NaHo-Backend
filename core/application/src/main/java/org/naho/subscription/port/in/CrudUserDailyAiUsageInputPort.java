package org.naho.subscription.port.in;

import org.naho.subscription.result.UserDailyAiUsageResult;

public interface CrudUserDailyAiUsageInputPort {
    UserDailyAiUsageResult findTodayUserDailyAiUsage(Long userId);
}
