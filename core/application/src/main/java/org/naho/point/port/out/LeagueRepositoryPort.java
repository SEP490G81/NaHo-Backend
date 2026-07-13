package org.naho.point.port.out;

import org.naho.file.model.File;
import org.naho.point.model.League;

import java.util.List;
import java.util.Map;

public interface LeagueRepositoryPort {
    List<League> findAll();

    Map<Long, File> findAllByLeagueId(List<Long> leagueIds);
}
