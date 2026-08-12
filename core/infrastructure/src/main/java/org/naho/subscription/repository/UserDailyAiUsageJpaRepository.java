package org.naho.subscription.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.subscription.entity.UserDailyAiUsageEntity;

import java.time.LocalDate;
import java.util.Optional;

public interface UserDailyAiUsageJpaRepository extends BaseJpaRepository<UserDailyAiUsageEntity> {
    Optional<UserDailyAiUsageEntity> findByUser_IdAndUsageDate(Long userId, LocalDate usageDate);
}
