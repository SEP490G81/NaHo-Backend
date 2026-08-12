package org.naho.subscription.port.in;

import org.naho.subscription.result.UserDailyAiUsageResult;

import java.time.LocalDate;

public interface CrudUserDailyAiUsageInputPort {
    UserDailyAiUsageResult findByUserIdAndUsageDate(Long userId, LocalDate usageDate);
}
