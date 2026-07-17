package org.naho.season.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.season.command.CreateSeasonCommand;
import org.naho.season.entity.SeasonEntity;
import org.naho.season.mapper.SeasonEntityMapper;
import org.naho.season.model.Season;
import org.naho.season.mybatis.SeasonQueryMapper;
import org.naho.season.port.out.SeasonRepositoryPort;
import org.naho.season.repository.SeasonJpaRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SeasonRepositoryAdapter implements SeasonRepositoryPort {

    private final SeasonJpaRepository seasonJpaRepository;
    private final SeasonQueryMapper seasonQueryMapper;
    private final SeasonEntityMapper seasonEntityMapper;

    @Override
    public Integer findLatestSeasonNo() {
        return seasonQueryMapper.findLatestSeasonNo();
    }

    @Override
    public Season save(Season season) {
        SeasonEntity seasonEntity = seasonEntityMapper.domainToEntity(season);
        SeasonEntity savedSeasonEntity = seasonJpaRepository.save(seasonEntity);
        return seasonEntityMapper.entityToDomain(savedSeasonEntity);
    }

    @Override
    public boolean existsOverlappingSeason(CreateSeasonCommand command) {
        return seasonQueryMapper.existsOverlappingSeason(command.startAt(), command.endAt());
    }
}
