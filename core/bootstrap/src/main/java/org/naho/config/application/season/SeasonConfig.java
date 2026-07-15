package org.naho.config.application.season;

import org.naho.season.mapper.SeasonResultMapper;
import org.naho.season.port.out.SeasonRepositoryPort;
import org.naho.season.usecase.CrudSeasonUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeasonConfig {

    @Bean
    public SeasonResultMapper seasonResultMapper() {
        return new SeasonResultMapper();
    }

    @Bean
    public CrudSeasonUseCase crudSeasonUseCase(
            SeasonRepositoryPort seasonRepositoryPort,
            SeasonResultMapper seasonResultMapper
    ) {
        return new CrudSeasonUseCase(
                seasonRepositoryPort,
                seasonResultMapper
        );
    }
}
