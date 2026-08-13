package org.naho.subscription.port.out;

import org.naho.subscription.model.UserDailyAiUsage;

import java.time.LocalDate;

public interface UserDailyAiUsageRepositoryPort {
    UserDailyAiUsage findByUserIdAndUsageDateCreateIfNotExists(Long userId, LocalDate usageDate);

    UserDailyAiUsage save(UserDailyAiUsage userDailyAiUsage);
}
