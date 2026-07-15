package org.naho.season.port.out;

import org.naho.season.command.CreateSeasonCommand;
import org.naho.season.model.Season;

public interface SeasonRepositoryPort {
    Integer findLatestSeasonNo();

    Season save(Season season);

    boolean existsOverlappingSeason(CreateSeasonCommand command);
}
