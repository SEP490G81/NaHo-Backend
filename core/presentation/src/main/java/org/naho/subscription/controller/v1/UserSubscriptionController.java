package org.naho.subscription.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.subscription.dto.mapper.UserSubscriptionResponseMapper;
import org.naho.subscription.dto.response.UserSubscriptionResponse;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.UserSubscriptionResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class UserSubscriptionController {

    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final UserSubscriptionResponseMapper userSubscriptionResponseMapper;

    /**
     * Lấy gói đăng kí hiện tại của người dùng đang đăng nhập
     *
     * @param payload chứa user id lấy từ JWT token
     * @return SubscriptionPlanResponse
     */
    @GetMapping("/me")
    @ApiResponseMessage(message = SubscriptionDetailMessageKey.SUBSCRIPTION_USER_GET_ACTIVE_SUCCESS)
    public ResponseEntity<UserSubscriptionResponse> getUserActiveSubscriptionPlan(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        UserSubscriptionResult result = getActiveSubscriptionInputPort
                .getUserActiveSubscription(payload.userId());

        UserSubscriptionResponse response = userSubscriptionResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy gói đăng kí hiện tại của người dùng theo id
     *
     * @param {id} la user id
     * @return SubscriptionPlanResponse
     */

    @GetMapping("/{userId}")
    @ApiResponseMessage(message = SubscriptionDetailMessageKey.SUBSCRIPTION_USER_GET_ACTIVE_SUCCESS)
    public ResponseEntity<UserSubscriptionResponse> getUserActiveSubscriptionPlan(
            @PathVariable("userId") Long id
    ) {
        UserSubscriptionResult result = getActiveSubscriptionInputPort
                .getUserActiveSubscription(id);

        UserSubscriptionResponse response = userSubscriptionResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }
}
