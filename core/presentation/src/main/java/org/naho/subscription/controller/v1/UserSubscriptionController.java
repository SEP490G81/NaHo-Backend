package org.naho.subscription.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.subscription.dto.mapper.SubscriptionResponseMapper;
import org.naho.subscription.dto.response.SubscriptionPlanResponse;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final SubscriptionResponseMapper subscriptionResponseMapper;

    /**
     * Lấy gói đăng kí hiện tại của người dùng đang đăng nhập
     *
     * @param payload chứa user id lấy từ JWT token
     * @return SubscriptionPlanResponse
     */
    @GetMapping("/me")
    @ApiResponseMessage(message = SubscriptionDetailMessageKey.SUBSCRIPTION_USER_GET_ACTIVE_SUCCESS)
    public ResponseEntity<SubscriptionPlanResponse> getUserActiveSubscriptionPlan(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        SubscriptionPlanResult result = getActiveSubscriptionInputPort
                .getUserActiveSubscriptionPlan(payload.userId());

        SubscriptionPlanResponse response = subscriptionResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }
}
