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
    public UserDailyMissionResultMapper userDailyMissionResultMapper(
            CrudDailyMissionInputPort crudDailyMissionInputPort,
            DailyMissionResultMapper dailyMissionResultMapper) {
        return new UserDailyMissionResultMapper(crudDailyMissionInputPort, dailyMissionResultMapper);
    }

    @Bean
    public CrudDailyMissionInputPort crudDailyMissionInputPort(
            DailyMissionRepositoryPort dailyMissionRepositoryPort,
            DailyMissionResultMapper dailyMissionResultMapper) {
        return new CrudDailyMissionUseCase(
                dailyMissionRepositoryPort,
                dailyMissionResultMapper);
    }

    @Bean
    public CrudUserDailyMissionInputPort crudUserDailyMissionInputPort(
            UserDailyMissionRepositoryPort userDailyMissionRepositoryPort,
            UserDailyMissionResultMapper userDailyMissionResultMapper,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            DailyMissionRepositoryPort dailyMissionRepositoryPort,
            CrudPointHistoryInputPort crudPointHistoryInputPort,
            TransactionPort transactionPort) {
        return new CrudUserDailyMissionUseCase(
                userDailyMissionRepositoryPort,
                userDailyMissionResultMapper,
                userLearningProgressRepositoryPort,
                dailyMissionRepositoryPort,
                crudPointHistoryInputPort,
                transactionPort);
    }
}
