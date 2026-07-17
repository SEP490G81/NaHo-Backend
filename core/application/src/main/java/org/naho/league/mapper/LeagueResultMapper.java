package org.naho.league.mapper;

import org.naho.file.result.FileResult;
import org.naho.league.model.League;
import org.naho.league.result.LeagueResult;

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
