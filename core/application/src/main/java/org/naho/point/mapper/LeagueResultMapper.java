package org.naho.point.mapper;

import org.naho.file.result.FileResult;
import org.naho.point.model.League;
import org.naho.point.result.LeagueResult;

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
