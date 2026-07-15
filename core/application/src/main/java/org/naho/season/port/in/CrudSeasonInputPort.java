package org.naho.season.port.in;

import org.naho.season.command.CreateSeasonCommand;
import org.naho.season.result.SeasonResult;

public interface CrudSeasonInputPort {
    SeasonResult createNextSeason(CreateSeasonCommand command);
}
