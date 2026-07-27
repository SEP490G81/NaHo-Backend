package org.naho.daily.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.daily.dto.mapper.CompleteMissionRequestMapper;
import org.naho.daily.dto.mapper.DailyMissionResponseMapper;
import org.naho.daily.dto.request.CompleteMissionRequest;
import org.naho.daily.dto.response.DailyMissionResponse;
import org.naho.daily.port.in.CrudDailyMissionInputPort;
import org.naho.daily.result.DailyMissionResult;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/daily-missions")
@RequiredArgsConstructor
public class DailyMissionController {
    private final CrudDailyMissionInputPort crudDailyMissionInputPort;
    private final DailyMissionResponseMapper dailyMissionResponseMapper;
    private final CompleteMissionRequestMapper completeMissionRequestMapper;

    @ApiResponseMessage(message = DailyMissionDetailMessageKey.DAILY_MISSION_GET_TODAY_SUCCESS)
    @GetMapping("/today")
    public ResponseEntity<List<DailyMissionResponse>> getTodayMissions() {
        List<DailyMissionResult> results = crudDailyMissionInputPort.getTodayMissions();

        List<DailyMissionResponse> responses = results.stream()
                .map(dailyMissionResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @ApiResponseMessage(message = DailyMissionDetailMessageKey.DAILY_MISSION_COMPLETE_SUCCESS)
    @PostMapping("/completion")
    public ResponseEntity<Void> completeMission(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody CompleteMissionRequest request
    ) {
        crudDailyMissionInputPort.completeMission(
                completeMissionRequestMapper.requestToCommand(request, payload.userId())
        );
        
        return ResponseEntity.ok().build();
    }
}

