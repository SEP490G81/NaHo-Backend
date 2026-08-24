package org.naho.subscription.controller.v1;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.subscription.command.UpdateSubscriptionPlanCommand;
import org.naho.subscription.dto.mapper.SubscriptionPlanRequestMapper;
import org.naho.subscription.dto.mapper.SubscriptionResponseMapper;
import org.naho.subscription.dto.request.UpdateSubscriptionPlanRequest;
import org.naho.subscription.dto.response.SubscriptionPlanResponse;
import org.naho.subscription.port.in.ListActivePlansInputPort;
import org.naho.subscription.port.in.UpdateSubscriptionPlanInputPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final ListActivePlansInputPort listActivePlansInputPort;
    private final UpdateSubscriptionPlanInputPort updateSubscriptionPlanInputPort;
    private final SubscriptionPlanRequestMapper subscriptionPlanRequestMapper;
    private final SubscriptionResponseMapper subscriptionResponseMapper;

    // ROLE: ADMIN, LEARNER

    /**
     * Lấy ra các Subscription Plan đang được ACTIVE
     *
     * @return List<SubscriptionPlanResponse>
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'LEARNER')")
    @GetMapping
    @ApiResponseMessage(message = SubscriptionDetailMessageKey.SUBSCRIPTION_PLANS_GET_LIST_SUCCESS)
    public ResponseEntity<List<SubscriptionPlanResponse>> listActivePlans() {
        List<SubscriptionPlanResult> results = listActivePlansInputPort.listActivePlans();

        List<SubscriptionPlanResponse> responses = results
                .stream().map(subscriptionResponseMapper::resultToResponse)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // ROLE: ADMIN

    /**
     * Admin cập nhật thông số của gói Subscription Plan
     *
     * @param id      ID của gói học cần cập nhật
     * @param request dữ liệu cập nhật
     * @param payload thông tin token người dùng
     * @return SubscriptionPlanResponse
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @ApiResponseMessage(message = SubscriptionDetailMessageKey.SUBSCRIPTION_PLAN_UPDATE_SUCCESS)
    public ResponseEntity<SubscriptionPlanResponse> updateSubscriptionPlan(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateSubscriptionPlanRequest request,
            @AuthenticationPrincipal AccessTokenPayload payload) {
        Long adminUserId = payload.userId();

        UpdateSubscriptionPlanCommand command = subscriptionPlanRequestMapper.toUpdateCommand(request, id, adminUserId);
        SubscriptionPlanResult result = updateSubscriptionPlanInputPort.updateSubscriptionPlan(command);
        SubscriptionPlanResponse response = subscriptionResponseMapper.resultToResponse(result);

        return ResponseEntity.ok(response);
    }
}
