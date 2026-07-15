package org.naho.season.usecase;

import org.naho.season.command.CreateSeasonCommand;
import org.naho.season.mapper.SeasonResultMapper;
import org.naho.season.model.Season;
import org.naho.season.port.in.CrudSeasonInputPort;
import org.naho.season.port.out.SeasonRepositoryPort;
import org.naho.season.result.SeasonResult;

public class CrudSeasonUseCase implements CrudSeasonInputPort {

    private final SeasonRepositoryPort seasonRepositoryPort;
    private final SeasonResultMapper seasonResultMapper;

    public CrudSeasonUseCase(
            SeasonRepositoryPort seasonRepositoryPort,
            SeasonResultMapper seasonResultMapper
    ) {
        this.seasonRepositoryPort = seasonRepositoryPort;
        this.seasonResultMapper = seasonResultMapper;
    }

    @Override
    public SeasonResult createNextSeason(CreateSeasonCommand command) {
        Integer lastestSeasonNo = seasonRepositoryPort.findLatestSeasonNo();

        if (lastestSeasonNo == null) {
            lastestSeasonNo = 0;
        }

        Season season = Season.builder()
                .seasonNo(lastestSeasonNo + 1)
                .startAt(command.startAt())
                .endAt(command.endAt())
                .build();

        Season savedSeason = seasonRepositoryPort.save(season);
        return seasonResultMapper.domainToResult(savedSeason);
    }
}
