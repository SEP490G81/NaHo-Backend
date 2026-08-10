package org.naho.subscription.controller.v1;

import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.subscription.dto.mapper.SubscriptionResponseMapper;
import org.naho.subscription.dto.response.SubscriptionPlanResponse;
import org.naho.subscription.port.in.ListActivePlansInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final ListActivePlansInputPort listActivePlansInputPort;
    private final SubscriptionResponseMapper subscriptionResponseMapper;

    /**
     * Lấy ra các Subscription Plan đang được ACTIVE
     *
     * @return List<SubscriptionPlanResponse>
     */
    @GetMapping
    @ApiResponseMessage(message = SubscriptionDetailMessageKey.SUBSCRIPTION_PLANS_GET_LIST_SUCCESS)
    public ResponseEntity<List<SubscriptionPlanResponse>> listActivePlans() {
        List<SubscriptionPlanResult> results = listActivePlansInputPort.listActivePlans();

        List<SubscriptionPlanResponse> responses = results
                .stream().map(subscriptionResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
