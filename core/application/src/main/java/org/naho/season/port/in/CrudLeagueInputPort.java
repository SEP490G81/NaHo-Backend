package org.naho.season.port.in;

import org.naho.season.result.LeagueResult;

import java.util.List;

public interface CrudLeagueInputPort {
    List<LeagueResult> findAll();
}
