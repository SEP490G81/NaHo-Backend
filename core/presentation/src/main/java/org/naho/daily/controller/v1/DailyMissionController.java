package org.naho.daily.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.daily.dto.mapper.DailyMissionResponseMapper;
import org.naho.daily.dto.response.DailyMissionResponse;
import org.naho.daily.port.in.CrudDailyMissionInputPort;
import org.naho.daily.result.DailyMissionResult;
import org.naho.i18n.message.daily.DailyMissionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/daily-missions")
@RequiredArgsConstructor
public class DailyMissionController {
    private final CrudDailyMissionInputPort crudDailyMissionInputPort;
    private final DailyMissionResponseMapper dailyMissionResponseMapper;

    @ApiResponseMessage(message = DailyMissionDetailMessageKey.DAILY_MISSION_GET_TODAY_SUCCESS)
    @GetMapping("/today")
    public ResponseEntity<List<DailyMissionResponse>> getTodayMissions() {
        List<DailyMissionResult> results = crudDailyMissionInputPort.getTodayMissions();

        List<DailyMissionResponse> responses = results.stream()
                .map(dailyMissionResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }
}

