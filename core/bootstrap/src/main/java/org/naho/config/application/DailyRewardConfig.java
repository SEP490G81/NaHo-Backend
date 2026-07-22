package org.naho.config.application;

import org.naho.chest.mapper.ChestResultMapper;
import org.naho.chest.port.out.ChestRepositoryPort;
import org.naho.daily.mapper.DailyRewardResultMapper;
import org.naho.daily.port.in.CrudDailyRewardInputPort;
import org.naho.daily.port.out.DailyRewardRepositoryPort;
import org.naho.daily.usecase.CrudDailyRewardUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DailyRewardConfig {

    @Bean
    public DailyRewardResultMapper dailyRewardResultMapper() {
        return new DailyRewardResultMapper();
    }

    @Bean
    public CrudDailyRewardInputPort crudDailyRewardInputPort(
            DailyRewardRepositoryPort dailyRewardRepositoryPort,
            DailyRewardResultMapper dailyRewardResultMapper,
            ChestRepositoryPort chestRepositoryPort,
            ChestResultMapper chestResultMapper
    ) {
        return new CrudDailyRewardUseCase(
                dailyRewardRepositoryPort,
                dailyRewardResultMapper,
                chestRepositoryPort,
                chestResultMapper
        );
    }
}
