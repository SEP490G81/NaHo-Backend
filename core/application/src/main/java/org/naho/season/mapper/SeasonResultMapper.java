package org.naho.season.mapper;

import org.naho.season.model.Season;
import org.naho.season.result.SeasonResult;

public class SeasonResultMapper {
    public SeasonResult domainToResult(Season domain) {
        return new SeasonResult(
                domain.getId(),
                domain.getSeasonNo(),
                domain.getStartAt(),
                domain.getEndAt()
        );
    }
}
