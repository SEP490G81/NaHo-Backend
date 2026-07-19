package org.naho.config.application.league;

import org.naho.file.port.in.CrudFileInputPort;
import org.naho.league.mapper.LeagueResultMapper;
import org.naho.league.port.out.LeagueRepositoryPort;
import org.naho.league.usecase.CrudLeagueUseCase;
import org.naho.user.port.out.UserRepositoryPort;
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
            CrudFileInputPort crudFileInputPort,
            UserRepositoryPort userRepositoryPort
    ) {
        return new CrudLeagueUseCase(
                leagueRepositoryPort,
                leagueResultMapper,
                crudFileInputPort,
                userRepositoryPort
        );
    }
}
