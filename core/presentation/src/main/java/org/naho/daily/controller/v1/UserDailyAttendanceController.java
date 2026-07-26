package org.naho.daily.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.daily.dto.mapper.UserDailyAttendanceResponseMapper;
import org.naho.daily.dto.response.UserDailyAttendanceResponse;
import org.naho.daily.port.in.CrudUserDailyAttendanceInputPort;
import org.naho.daily.result.UserDailyAttendanceResult;
import org.naho.i18n.message.daily.UserDailyAttendanceDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-daily-attendances")
public class UserDailyAttendanceController {
    private final CrudUserDailyAttendanceInputPort crudUserDailyAttendanceInputPort;
    private final UserDailyAttendanceResponseMapper userDailyAttendanceResponseMapper;

    @ApiResponseMessage(message = UserDailyAttendanceDetailMessageKey.USER_DAILY_ATTENDANCE_GET_CURRENT_MONTH_SUCCESS)
    @GetMapping("/all/current-month")
    public ResponseEntity<List<UserDailyAttendanceResponse>> findAllUserDailyAttendanceOfCurrentMonth(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        List<UserDailyAttendanceResult> results = crudUserDailyAttendanceInputPort
                .findAllUserDailyAttendanceOfCurrentMonth(payload.userId());

        List<UserDailyAttendanceResponse> responses = results.stream()
                .map(userDailyAttendanceResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
