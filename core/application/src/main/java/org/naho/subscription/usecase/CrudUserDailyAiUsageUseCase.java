package org.naho.subscription.usecase;

import org.naho.shared.constant.SystemZoneId;
import org.naho.subscription.mapper.UserDailyAiUsageResultMapper;
import org.naho.subscription.model.UserDailyAiUsage;
import org.naho.subscription.port.in.CrudUserDailyAiUsageInputPort;
import org.naho.subscription.port.out.UserDailyAiUsageRepositoryPort;
import org.naho.subscription.result.UserDailyAiUsageResult;

import java.time.LocalDate;

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
     * Method lấy ra usage AI của người dùng trong ngày hôm nay
     *
     * @param userId mã người dùng
     * @return UserDailyAiUsageResult
     */
    @Override
    public UserDailyAiUsageResult findTodayUserDailyAiUsage(Long userId) {
        LocalDate today = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        UserDailyAiUsage userDailyAiUsage = userDailyAiUsageRepositoryPort
                .findByUserIdAndUsageDateCreateIfNotExists(userId, today);

        return userDailyAiUsageResultMapper.domainToResult(userDailyAiUsage);
    }
}
