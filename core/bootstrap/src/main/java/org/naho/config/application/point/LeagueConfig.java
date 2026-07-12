package org.naho.config.application.point;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.point.mapper.LeagueResultMapper;
import org.naho.point.port.out.LeagueRepositoryPort;
import org.naho.point.usecase.CrudLeagueUseCase;
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
