package org.naho.league.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.model.File;
import org.naho.league.entity.LeagueEntity;
import org.naho.league.mapper.LeagueEntityMapper;
import org.naho.league.model.League;
import org.naho.league.mybatis.LeagueQueryMapper;
import org.naho.league.port.out.LeagueRepositoryPort;
import org.naho.league.repository.LeagueJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LeagueRepositoryAdapter implements LeagueRepositoryPort {

    private final LeagueJpaRepository leagueJpaRepository;
    private final LeagueEntityMapper leagueEntityMapper;
    private final LeagueQueryMapper leagueQueryMapper;

    @Override
    public List<League> findAll() {
        List<LeagueEntity> leagueEntityList = leagueQueryMapper.findAll();
        return leagueEntityList
                .stream()
                .map(leagueEntityMapper::entityToDomain)
                .toList();
    }

    @Override
    public Map<Long, File> findAllByLeagueId(List<Long> leagueIds) {
        return Map.of();
    }
}
