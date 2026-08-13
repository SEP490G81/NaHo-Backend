package org.naho.subscription.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.subscription.entity.UserDailyAiUsageEntity;
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

    /**
     * Method tìm số lượt dùng AI của người dùng bằng user id và ngày dùng
     * Nếu chưa có thì sẽ tạo mới
     *
     * @param userId    mã người dùng
     * @param usageDate ngày sử dụng
     * @return UserDailyAiUsage
     */
    @Override
    public UserDailyAiUsage findByUserIdAndUsageDateCreateIfNotExists(Long userId, LocalDate usageDate) {
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

        Optional<UserDailyAiUsageEntity> currentEntity = userDailyAiUsageJpaRepository
                .findByUser_IdAndUsageDate(userId, usageDate);

        // nếu đã tồn tại thì trả về
        if (currentEntity.isPresent()) {
            return userDailyAiUsageEntityMapper.entityToDomain(currentEntity.get());
        }

        // nếu chưa thì init
        UserDailyAiUsage userDailyAiUsage = UserDailyAiUsage.init(userId, usageDate);
        return this.save(userDailyAiUsage);
    }

    @Override
    public UserDailyAiUsage save(UserDailyAiUsage userDailyAiUsage) {
        UserDailyAiUsageEntity entity = userDailyAiUsageEntityMapper.domainToEntity(userDailyAiUsage);
        UserDailyAiUsageEntity savedEntity = userDailyAiUsageJpaRepository.save(entity);
        return userDailyAiUsageEntityMapper.entityToDomain(savedEntity);
    }
}
