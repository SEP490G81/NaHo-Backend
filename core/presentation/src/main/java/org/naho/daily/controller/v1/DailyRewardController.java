package org.naho.daily.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.daily.dto.mapper.DailyRewardResponseMapper;
import org.naho.daily.dto.response.DailyRewardResponse;
import org.naho.daily.port.in.CrudDailyRewardInputPort;
import org.naho.daily.result.DailyRewardResult;
import org.naho.i18n.message.daily.DailyRewardDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/daily-rewards")
@RequiredArgsConstructor
public class DailyRewardController {

    private final CrudDailyRewardInputPort crudDailyRewardInputPort;
    private final DailyRewardResponseMapper dailyRewardResponseMapper;

    @ApiResponseMessage(message = DailyRewardDetailMessageKey.DAILY_REWARD_GET_CURRENT_MONTH_SUCCESS)
    @GetMapping("/current-month")
    public ResponseEntity<List<DailyRewardResponse>> getCurrentMonthDailyRewards() {
        List<DailyRewardResult> results = crudDailyRewardInputPort.getCurrentMonthDailyRewards();
        List<DailyRewardResponse> responses = results.stream()
                .map(dailyRewardResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @ApiResponseMessage(message = DailyRewardDetailMessageKey.DAILY_REWARD_CREATE_CURRENT_MONTH_SUCCESS)
    @PostMapping("/current-month")
    public ResponseEntity<List<DailyRewardResponse>> createCurrentMonthDailyRewards() {
        List<DailyRewardResult> results = crudDailyRewardInputPort.createCurrentMonthDailyRewards();
        List<DailyRewardResponse> responses = results.stream()
                .map(dailyRewardResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}
