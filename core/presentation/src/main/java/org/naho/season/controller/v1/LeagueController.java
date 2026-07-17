package org.naho.season.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.season.LeagueDetailMessageKey;
import org.naho.season.dto.mapper.LeagueResponseMapper;
import org.naho.season.dto.response.LeagueResponse;
import org.naho.season.port.in.CrudLeagueInputPort;
import org.naho.season.result.LeagueResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leagues")
@RequiredArgsConstructor
public class LeagueController {
    private final CrudLeagueInputPort crudLeagueInputPort;
    private final LeagueResponseMapper leagueResponseMapper;

    @ApiResponseMessage(message = LeagueDetailMessageKey.LEAGUE_GET_ALL_SUCCESS)
    @GetMapping("/all")
    public ResponseEntity<List<LeagueResponse>> findAll() {
        List<LeagueResult> leagueResultList = crudLeagueInputPort.findAll();

        List<LeagueResponse> leagueResponseList = leagueResultList.stream()
                .map(leagueResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(leagueResponseList);
    }
}
