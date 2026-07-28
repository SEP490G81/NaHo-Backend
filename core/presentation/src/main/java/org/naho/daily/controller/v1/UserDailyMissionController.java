package org.naho.daily.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.daily.command.EarnDailyMissionCommand;
import org.naho.daily.dto.mapper.UserDailyMissionResponseMapper;
import org.naho.daily.dto.request.EarnDailyMissionRequest;
import org.naho.daily.dto.response.UserDailyMissionResponse;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.daily.result.UserDailyMissionResult;
import org.naho.i18n.message.daily.UserDailyMissionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-daily-missions")
@RequiredArgsConstructor
public class UserDailyMissionController {
    private final CrudUserDailyMissionInputPort crudUserDailyMissionInputPort;
    private final UserDailyMissionResponseMapper userDailyMissionResponseMapper;

    @ApiResponseMessage(message = UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_GET_TODAY_SUCCESS)
    @GetMapping("/today")
    public ResponseEntity<List<UserDailyMissionResponse>> findAllUserTodayMissions(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<UserDailyMissionResult> results = crudUserDailyMissionInputPort
                .findAllUserTodayMissions(payload.userId());

        List<UserDailyMissionResponse> responses = results.stream()
                .map(userDailyMissionResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    @ApiResponseMessage(message = UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_EARN_SUCCESS)
    @PostMapping("/earn")
    public ResponseEntity<UserDailyMissionResponse> earnMission(
            @RequestBody EarnDailyMissionRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        EarnDailyMissionCommand command = new EarnDailyMissionCommand(
                request.userDailyMissionId(),
                payload.userId()
        );

        UserDailyMissionResult result = crudUserDailyMissionInputPort.earnMission(command);
        UserDailyMissionResponse response = userDailyMissionResponseMapper.resultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
