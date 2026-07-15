package org.naho.season.mapper;

import org.naho.file.result.FileResult;
import org.naho.season.model.League;
import org.naho.season.result.LeagueResult;

public class LeagueResultMapper {
    public LeagueResult domainToResult(League league, FileResult iconFile) {
        return new LeagueResult(
                league.getId(),
                iconFile,
                league.getName(),
                league.getDescription(),
                league.getMinPoint(),
                league.getMaxPoint()
        );
    }
}
