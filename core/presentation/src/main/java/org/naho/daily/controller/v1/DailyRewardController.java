package org.naho.daily.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.daily.command.EarnDailyRewardCommand;
import org.naho.daily.dto.mapper.DailyRewardResponseMapper;
import org.naho.daily.dto.mapper.UserDailyAttendanceResponseMapper;
import org.naho.daily.dto.request.EarnDailyRewardRequest;
import org.naho.daily.dto.response.DailyRewardResponse;
import org.naho.daily.dto.response.UserDailyAttendanceResponse;
import org.naho.daily.port.in.CrudDailyRewardInputPort;
import org.naho.daily.result.DailyRewardResult;
import org.naho.daily.result.UserDailyAttendanceResult;
import org.naho.i18n.message.daily.DailyRewardDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.constant.SystemZoneId;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/v1/daily-rewards")
@RequiredArgsConstructor
public class DailyRewardController {

    private final CrudDailyRewardInputPort crudDailyRewardInputPort;
    private final DailyRewardResponseMapper dailyRewardResponseMapper;
    private final UserDailyAttendanceResponseMapper userDailyAttendanceResponseMapper;

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = DailyRewardDetailMessageKey.DAILY_REWARD_GET_CURRENT_MONTH_SUCCESS)
    @GetMapping("/current-month")
    public ResponseEntity<List<DailyRewardResponse>> getCurrentMonthDailyRewards() {
        List<DailyRewardResult> results = crudDailyRewardInputPort.getCurrentMonthDailyRewards();
        List<DailyRewardResponse> responses = results.stream()
                .map(dailyRewardResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = DailyRewardDetailMessageKey.DAILY_REWARD_CREATE_CURRENT_MONTH_SUCCESS)
    @PostMapping("/current-month")
    public ResponseEntity<List<DailyRewardResponse>> createCurrentMonthDailyRewards() {
        YearMonth yearMonth = YearMonth.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        List<DailyRewardResult> results = crudDailyRewardInputPort
                .createMonthlyDailyRewards(yearMonth);

        List<DailyRewardResponse> responses = results.stream()
                .map(dailyRewardResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = DailyRewardDetailMessageKey.DAILY_REWARD_EARN_SUCCESS)
    @PostMapping
    public ResponseEntity<UserDailyAttendanceResponse> earnDailyReward(
            @RequestBody EarnDailyRewardRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        EarnDailyRewardCommand command = new EarnDailyRewardCommand(
                payload.userId(),
                request.dailyRewardId()
        );

        UserDailyAttendanceResult result =
                crudDailyRewardInputPort.earnDailyReward(command);

        UserDailyAttendanceResponse response =
                userDailyAttendanceResponseMapper.resultToResponse(result);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
