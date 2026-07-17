package org.naho.season.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.season.SeasonDetailMessageKey;
import org.naho.season.command.CreateSeasonCommand;
import org.naho.season.dto.mapper.SeasonRequestMapper;
import org.naho.season.dto.mapper.SeasonResponseMapper;
import org.naho.season.dto.request.CreateSeasonRequest;
import org.naho.season.dto.response.SeasonResponse;
import org.naho.season.port.in.CrudSeasonInputPort;
import org.naho.season.result.SeasonResult;
import org.naho.shared.annotation.ApiResponseMessage;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seasons")
@RequiredArgsConstructor
public class SeasonController {
    private final CrudSeasonInputPort crudSeasonInputPort;
    private final SeasonRequestMapper seasonRequestMapper;
    private final SeasonResponseMapper seasonResponseMapper;

    @ApiResponseMessage(message = SeasonDetailMessageKey.SEASON_CREATE_SUCCESS)
    @PostMapping("/next")
    public ResponseEntity<SeasonResponse> createNextSeason(
            @Valid @RequestBody CreateSeasonRequest request
    ) {
        CreateSeasonCommand command = seasonRequestMapper.requestToCommand(request);
        SeasonResult result = crudSeasonInputPort.createNextSeason(command);
        SeasonResponse response = seasonResponseMapper.resultToResponse(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
