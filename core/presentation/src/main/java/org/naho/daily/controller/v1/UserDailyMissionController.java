package org.naho.daily.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.daily.dto.mapper.UserDailyMissionResponseMapper;
import org.naho.daily.dto.response.UserDailyMissionResponse;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.daily.result.UserDailyMissionResult;
import org.naho.i18n.message.daily.UserDailyMissionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-daily-missions")
@RequiredArgsConstructor
public class UserDailyMissionController {
    private final CrudUserDailyMissionInputPort crudUserDailyMissionInputPort;
    private final UserDailyMissionResponseMapper userDailyMissionResponseMapper;

    @ApiResponseMessage(message = UserDailyMissionDetailMessageKey.USER_DAILY_MISSION_GET_ALL_SUCCESS)
    @GetMapping("/all")
    public ResponseEntity<List<UserDailyMissionResponse>> findAllByUserId(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<UserDailyMissionResult> results = crudUserDailyMissionInputPort.findAllByUserId(payload.userId());
        
        List<UserDailyMissionResponse> responses = results.stream()
                .map(userDailyMissionResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
