package org.naho.league.port.in;

import org.naho.league.result.LeagueResult;
import org.naho.user.result.LeaderboardUserResult;

import java.util.List;

public interface CrudLeagueInputPort {
    List<LeagueResult> findAll();

    List<LeaderboardUserResult> findTop10OrderByTotalPointInLeague(Long leagueId);

    LeaderboardUserResult findTopOfUserByUserId(Long userId);
}
