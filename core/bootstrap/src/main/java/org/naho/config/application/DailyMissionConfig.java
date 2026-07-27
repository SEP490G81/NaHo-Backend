package org.naho.config.application;

import org.naho.daily.mapper.DailyMissionResultMapper;
import org.naho.daily.mapper.UserDailyMissionResultMapper;
import org.naho.daily.port.in.CrudDailyMissionInputPort;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.daily.port.out.DailyMissionRepositoryPort;
import org.naho.daily.port.out.UserDailyMissionRepositoryPort;
import org.naho.daily.usecase.CrudDailyMissionUseCase;
import org.naho.daily.usecase.CrudUserDailyMissionUseCase;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.point.port.in.CrudPointHistoryInputPort;
import org.naho.shared.port.out.TransactionPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DailyMissionConfig {

    @Bean
    public DailyMissionResultMapper dailyMissionResultMapper() {
        return new DailyMissionResultMapper();
    }

    @Bean
    public UserDailyMissionResultMapper userDailyMissionResultMapper() {
        return new UserDailyMissionResultMapper();
    }

    @Bean
    public CrudDailyMissionInputPort crudDailyMissionInputPort(
            DailyMissionRepositoryPort dailyMissionRepositoryPort,
            UserDailyMissionRepositoryPort userDailyMissionRepositoryPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            DailyMissionResultMapper dailyMissionResultMapper,
            TransactionPort transactionPort
    ) {
        return new CrudDailyMissionUseCase(
                dailyMissionRepositoryPort,
                userDailyMissionRepositoryPort,
                userLearningProgressRepositoryPort,
                crudPointHistoryInputPort,
                dailyMissionResultMapper,
                transactionPort
        );
    }

    @Bean
    public CrudUserDailyMissionInputPort crudUserDailyMissionInputPort(
            UserDailyMissionRepositoryPort userDailyMissionRepositoryPort,
            UserDailyMissionResultMapper userDailyMissionResultMapper
    ) {
        return new CrudUserDailyMissionUseCase(
                userDailyMissionRepositoryPort,
                userDailyMissionResultMapper
        );
    }
}
