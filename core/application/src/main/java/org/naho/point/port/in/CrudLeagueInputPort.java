package org.naho.point.port.in;

import org.naho.point.result.LeagueResult;

import java.util.List;

public interface CrudLeagueInputPort {
    List<LeagueResult> findAll();
}
