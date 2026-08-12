package org.naho.subscription.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.subscription.exception.SubscriptionDomainErrorCode;
import org.naho.subscription.mapper.UserDailyAiUsageEntityMapper;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
import org.naho.subscription.repository.UserDailyAiUsageJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDailyAiUsageRepositoryAdapter implements UserDailyAiUsageRepositoryPort {
    private final UserDailyAiUsageJpaRepository userDailyAiUsageJpaRepository;
    private final UserDailyAiUsageEntityMapper userDailyAiUsageEntityMapper;

    @Override
    public Optional<UserDailyAiUsage> findByUserIdAndUsageDate(Long userId, LocalDate usageDate) {
        if (userId == null) {
            throw new InfrastructureException(
                    SubscriptionDomainErrorCode.SUBSCRIPTION_USER_ID_EMPTY,
                    SubscriptionDetailMessageKey.SUBSCRIPTION_USER_ID_EMPTY
            );
        }
        if (usageDate == null) {
            throw new InfrastructureException(
                    SubscriptionDomainErrorCode.SUBSCRIPTION_TIME_INVALID,
                    SubscriptionDetailMessageKey.SUBSCRIPTION_TIME_INVALID
            );
        }

        return userDailyAiUsageJpaRepository
                .findByUser_IdAndUsageDate(userId, usageDate)
                .map(userDailyAiUsageEntityMapper::entityToDomain);
    }
}
