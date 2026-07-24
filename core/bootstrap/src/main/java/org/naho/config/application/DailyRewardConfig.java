package org.naho.config.application;

import org.naho.chest.mapper.ChestResultMapper;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.daily.mapper.DailyRewardResultMapper;
import org.naho.daily.mapper.UserDailyAttendanceResultMapper;
import org.naho.daily.port.in.CrudDailyRewardInputPort;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.port.out.UserDailyAttendanceRepositoryPort;
import org.naho.daily.usecase.CrudDailyRewardUseCase;
import org.naho.learning.port.in.CrudUserLearningProgressInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DailyRewardConfig {

    @Bean
    public DailyRewardResultMapper dailyRewardResultMapper(
            ChestRepositoryPort chestRepositoryPort,
            ChestResultMapper chestResultMapper
    ) {
        return new DailyRewardResultMapper(
                chestRepositoryPort,
                chestResultMapper
        );
    }

    @Bean
    public UserDailyAttendanceResultMapper userDailyAttendanceResultMapper() {
        return new UserDailyAttendanceResultMapper();
    }

    @Bean
    public CrudDailyRewardInputPort crudDailyRewardInputPort(
            DailyRewardRepositoryPort dailyRewardRepositoryPort,
            DailyRewardResultMapper dailyRewardResultMapper,
            ChestRepositoryPort chestRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            UserDailyAttendanceRepositoryPort userDailyAttendanceRepositoryPort,
            UserDailyAttendanceResultMapper userDailyAttendanceResultMapper,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            CrudUserLearningProgressInputPort crudUserLearningProgressInputPort,
            TransactionPort transactionPort
    ) {
        return new CrudDailyRewardUseCase(
                dailyRewardRepositoryPort,
                dailyRewardResultMapper,
                chestRepositoryPort,
                userLearningProgressRepositoryPort,
                userDailyAttendanceRepositoryPort,
                userDailyAttendanceResultMapper,
                crudPointHistoryInputPort,
                crudUserLearningProgressInputPort,
                transactionPort
        );
    }
}
