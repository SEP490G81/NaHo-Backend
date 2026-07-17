package org.naho.config.application.season;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.season.mapper.LeagueResultMapper;
import org.naho.season.port.out.LeagueRepositoryPort;
import org.naho.season.usecase.CrudLeagueUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LeagueConfig {
    @Bean
    public LeagueResultMapper leagueResultMapper() {
        return new LeagueResultMapper();
    }

    @Bean
    public CrudLeagueUseCase crudLeagueUseCase(
            LeagueRepositoryPort leagueRepositoryPort,
            LeagueResultMapper leagueResultMapper,
            CrudFileInputPort crudFileInputPort
    ) {
        return new CrudLeagueUseCase(
                leagueRepositoryPort,
                leagueResultMapper,
                crudFileInputPort
        );
    }
}
