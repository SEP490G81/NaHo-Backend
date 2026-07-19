package org.naho.league.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.league.LeagueDetailMessageKey;
import org.naho.league.dto.mapper.LeagueResponseMapper;
import org.naho.league.dto.response.LeagueResponse;
import org.naho.league.port.in.CrudLeagueInputPort;
import org.naho.league.result.LeagueResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.dto.mapper.UserResponseMapper;
import org.naho.user.dto.response.LeaderboardUserResponse;
import org.naho.user.result.LeaderboardUserResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leagues")
@RequiredArgsConstructor
public class LeagueController {
    private final CrudLeagueInputPort crudLeagueInputPort;
    private final LeagueResponseMapper leagueResponseMapper;
    private final UserResponseMapper userResponseMapper;

    @ApiResponseMessage(message = LeagueDetailMessageKey.LEAGUE_GET_ALL_SUCCESS)
    @GetMapping("/all")
    public ResponseEntity<List<LeagueResponse>> findAll() {
        List<LeagueResult> leagueResultList = crudLeagueInputPort.findAll();

        List<LeagueResponse> leagueResponseList = leagueResultList.stream()
                .map(leagueResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(leagueResponseList);
    }

    @ApiResponseMessage(message = LeagueDetailMessageKey.LEAGUE_GET_LEADERBOARD_SUCCESS)
    @GetMapping("leaderboard/{leagueId}")
    public ResponseEntity<List<LeaderboardUserResponse>> findTop10OrderByTotalPointInLeague(
            @PathVariable Long leagueId
    ) {
        List<LeaderboardUserResult> results =
                crudLeagueInputPort.findTop10OrderByTotalPointInLeague(leagueId);
        List<LeaderboardUserResponse> responses = results
                .stream()
                .map(userResponseMapper::resultToResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }
}
