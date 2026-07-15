package org.naho.season.port.out;

import org.naho.file.model.File;
import org.naho.season.model.League;

import java.util.List;
import java.util.Map;

public interface LeagueRepositoryPort {
    List<League> findAll();

    Map<Long, File> findAllByLeagueId(List<Long> leagueIds);
}
