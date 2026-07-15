package org.naho.season.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.model.File;
import org.naho.season.entity.LeagueEntity;
import org.naho.season.mapper.LeagueEntityMapper;
import org.naho.season.model.League;
import org.naho.season.mybatis.LeagueQueryMapper;
import org.naho.season.port.out.LeagueRepositoryPort;
import org.naho.season.repository.LeagueJpaRepository;
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
