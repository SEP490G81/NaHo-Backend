package org.naho.subscription.usecase;

import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.mapper.UserDailyAiUsageResultMapper;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.in.CrudUserDailyAiUsageInputPort;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
import org.naho.subscription.result.UserDailyAiUsageResult;

import java.time.LocalDate;
import java.util.Optional;

public class CrudUserDailyAiUsageUseCase implements CrudUserDailyAiUsageInputPort {
    private final UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort;
    private final UserDailyAiUsageResultMapper userDailyAiUsageResultMapper;

    public CrudUserDailyAiUsageUseCase(
            UserDailyAiUsageRepositoryPort userDailyAiUsageRepositoryPort,
            UserDailyAiUsageResultMapper userDailyAiUsageResultMapper
    ) {
        this.userDailyAiUsageRepositoryPort = userDailyAiUsageRepositoryPort;
        this.userDailyAiUsageResultMapper = userDailyAiUsageResultMapper;
    }

    /**
     * Method tìm usage AI của người dùng theo user id và ngày dùng
     * nếu chưa có thì tạo mới
     *
     * @param userId    user id
     * @param usageDate ngày dùng
     * @return UserDailyAiUsage
     */
    @Override
    public UserDailyAiUsage findByUserIdAndUsageDate(Long userId, LocalDate usageDate) {
        Optional<UserDailyAiUsage> currentUserDailyAiUsage = userDailyAiUsageRepositoryPort
                .findByUserIdAndUsageDate(userId, usageDate);

        // nếu đã tồn tại thì trả về
        if (currentUserDailyAiUsage.isPresent()) {
            return currentUserDailyAiUsage.get();
        }

        // nếu chưa tồn tại
        // khởi tạo với số lượt sử dụng AI = 0
        UserDailyAiUsage userDailyAiUsage = UserDailyAiUsage.init(userId, usageDate);
        return userDailyAiUsageRepositoryPort.save(userDailyAiUsage);
    }

    @Override
    public UserDailyAiUsageResult findTodayUserDailyAiUsage(Long userId) {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        return userDailyAiUsageRepositoryPort
                .findByUserIdAndUsageDate(userId, today)
                .map(userDailyAiUsageResultMapper::domainToResult)
                .orElseThrow(() -> new ApplicationException(
                        SubscriptionErrorCode.USER_DAILY_AI_USAGE_NOT_FOUND,
                        SubscriptionDetailMessageKey.USER_DAILY_AI_USAGE_NOT_FOUND
                ));
    }
}
