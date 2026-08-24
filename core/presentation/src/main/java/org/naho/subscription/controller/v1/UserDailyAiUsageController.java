package org.naho.subscription.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.subscription.dto.mapper.UserDailyAiUsageResponseMapper;
import org.naho.subscription.dto.response.UserDailyAiUsageResponse;
import org.naho.subscription.port.in.CrudUserDailyAiUsageInputPort;
import org.naho.subscription.result.UserDailyAiUsageResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user-daily-ai-usages")
@RequiredArgsConstructor
public class UserDailyAiUsageController {
    private final CrudUserDailyAiUsageInputPort crudUserDailyAiUsageInputPort;
    private final UserDailyAiUsageResponseMapper userDailyAiUsageResponseMapper;

    // ROLE: LEARNER
    @PreAuthorize("hasRole('LEARNER')")
    @ApiResponseMessage(message = SubscriptionDetailMessageKey.USER_DAILY_AI_USAGE_GET_TODAY_SUCCESS)
    @GetMapping("/today")
    public ResponseEntity<UserDailyAiUsageResponse> findTodayUserDailyAiUsage(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        UserDailyAiUsageResult userDailyAiUsage = crudUserDailyAiUsageInputPort
                .findTodayUserDailyAiUsage(payload.userId());
        return ResponseEntity.ok(userDailyAiUsageResponseMapper.resultToResponse(userDailyAiUsage));
    }
}

