package org.naho.subscription.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.subscription.dto.mapper.SubscriptionResponseMapper;
import org.naho.subscription.dto.response.SubscriptionPlanResponse;
import org.naho.subscription.dto.response.UserSubscriptionResponse;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.in.ListActivePlansInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final ListActivePlansInputPort listActivePlansInputPort;
    private final GetActiveSubscriptionInputPort getActiveSubscriptionInputPort;
    private final SubscriptionResponseMapper responseMapper;

    @GetMapping
    @ApiResponseMessage(message = "subscription.plans.get_list_success")
    public ResponseEntity<List<SubscriptionPlanResponse>> listActivePlans() {
        List<SubscriptionPlanResult> results = listActivePlansInputPort.listActivePlans();
        List<SubscriptionPlanResponse> response = responseMapper.listPlanResultToResponse(results);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active-user-subscription")
    @ApiResponseMessage(message = "subscription.user.get_active_success")
    public ResponseEntity<UserSubscriptionResponse> getActiveSubscription(
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
