package org.naho.point.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.model.File;
import org.naho.point.entity.LeagueEntity;
import org.naho.point.mapper.LeagueEntityMapper;
import org.naho.point.model.League;
import org.naho.point.mybatis.LeagueQueryMapper;
import org.naho.point.port.out.LeagueRepositoryPort;
import org.naho.point.repository.LeagueJpaRepository;
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
