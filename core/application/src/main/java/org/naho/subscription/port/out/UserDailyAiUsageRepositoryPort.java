package org.naho.subscription.port.out;

import org.naho.subscription.model.UserDailyAiUsage;

import java.time.LocalDate;
import java.util.Optional;

public interface UserDailyAiUsageRepositoryPort {
    Optional<UserDailyAiUsage> findByUserIdAndUsageDate(Long userId, LocalDate usageDate);
}
