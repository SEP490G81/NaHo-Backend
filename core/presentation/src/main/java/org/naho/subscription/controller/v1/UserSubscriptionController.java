package org.naho.subscription.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.subscription.dto.mapper.SubscriptionResponseMapper;
import org.naho.subscription.dto.response.UserSubscriptionResponse;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.result.UserSubscriptionResult;
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
    private final SubscriptionResponseMapper responseMapper;

    @GetMapping("/me")
    @ApiResponseMessage(message = "subscription.user.get_active_success")
    public ResponseEntity<UserSubscriptionResponse> getMyActiveSubscription(
            @AuthenticationPrincipal AccessTokenPayload payload
    ) {
        UserSubscriptionResult result = getActiveSubscriptionInputPort.getActiveSubscription(payload.userId());
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        UserSubscriptionResponse response = responseMapper.userSubResultToResponse(result);
        return ResponseEntity.ok(response);
    }
}
