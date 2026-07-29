package org.naho.payment.controller.v1;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.command.AdminUpgradeSubscriptionCommand;
import org.naho.payment.command.CancelPaymentCommand;
import org.naho.payment.command.ConfirmPaymentCommand;
import org.naho.payment.command.CreatePaymentCommand;
import org.naho.payment.dto.mapper.PaymentResponseMapper;
import org.naho.payment.dto.request.AdminUpgradeSubscriptionRequest;
import org.naho.payment.dto.request.CreatePaymentRequest;
import org.naho.payment.dto.response.CancelPaymentResponse;
import org.naho.payment.dto.response.CreatePaymentResponse;
import org.naho.payment.dto.response.PaymentOrderResponse;
import org.naho.payment.dto.response.VnPayIpnResponse;
import org.naho.payment.helper.VnPayCallbackHelper;
import org.naho.payment.port.in.*;
import org.naho.payment.result.CancelPaymentResult;
import org.naho.payment.result.ConfirmPaymentResult;
import org.naho.payment.result.CreatePaymentResult;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.dto.mapper.SubscriptionResponseMapper;
import org.naho.subscription.dto.response.UserSubscriptionResponse;
import org.naho.subscription.result.UserSubscriptionResult;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final CreatePaymentInputPort createPaymentInputPort;
    private final ConfirmPaymentInputPort confirmPaymentInputPort;
    private final GetPaymentInputPort getPaymentInputPort;
    private final CancelPaymentInputPort cancelPaymentInputPort;
    private final AdminUpgradeSubscriptionInputPort adminUpgradeSubscriptionInputPort;
    private final PaymentResponseMapper responseMapper;
    private final SubscriptionResponseMapper subscriptionResponseMapper;
    private final VnPayCallbackHelper vnPayCallbackHelper;
    @Value("${app.frontend-url:http://localhost:3636}")
    private String frontendUrl;

    @GetMapping("/my-orders")
    @ApiResponseMessage(message = PaymentDetailMessageKey.PAYMENT_ORDER_GET_ALL_SUCCESS)
    public ResponseEntity<List<PaymentOrderResponse>> getMyPaymentOrders(
            @AuthenticationPrincipal AccessTokenPayload payload) {
        List<PaymentOrderResult> results = getPaymentInputPort.getPaymentsByUserId(payload.userId());
        List<PaymentOrderResponse> response = results.stream()
                .map(responseMapper::resultToOrderResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/upgrade-subscription")
    @ApiResponseMessage(message = PaymentDetailMessageKey.PAYMENT_SUBSCRIPTION_UPGRADE_SUCCESS)
    public ResponseEntity<UserSubscriptionResponse> upgradeUserSubscription(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody @Valid AdminUpgradeSubscriptionRequest request) {
        AdminUpgradeSubscriptionCommand command = new AdminUpgradeSubscriptionCommand(
                payload.userId(),
                request.getUserId(),
                request.getPlanCode(),
                request.getDurationDays()
        );
        UserSubscriptionResult result = adminUpgradeSubscriptionInputPort.upgradeSubscription(command);
        UserSubscriptionResponse response = subscriptionResponseMapper.userSubResultToResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create")
    @ApiResponseMessage(message = "payment.order.creation_success")
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpServletRequest) {
        String clientIp = vnPayCallbackHelper.extractClientIp(httpServletRequest);

        CreatePaymentCommand command = new CreatePaymentCommand(
                payload.userId(),
                request.getPlanCode(),
                request.getProvider(),
                clientIp,
                httpServletRequest.getLocale().getLanguage(),
                idempotencyKey);

        CreatePaymentResult result = createPaymentInputPort.createPayment(command);
        CreatePaymentResponse response = responseMapper.resultToCreateResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<VnPayIpnResponse> receiveVnPayIpn(
            @RequestParam Map<String, String> queryParams) {
        if (!vnPayCallbackHelper.verifyVnPaySignature(queryParams)) {
            return ResponseEntity.ok(responseMapper.toVnPayIpnResponse("97", "Invalid Signature"));
        }

        try {
            ConfirmPaymentCommand command = vnPayCallbackHelper.buildVnPayConfirmCommand(queryParams);
            ConfirmPaymentResult result = confirmPaymentInputPort.confirmPayment(command);
            return ResponseEntity.ok(vnPayCallbackHelper.mapIpnStatusToResponse(result.status()));
        } catch (ApplicationException e) {
            return ResponseEntity.ok(vnPayCallbackHelper.mapIpnApplicationExceptionToResponse(e));
        } catch (Exception e) {
            return ResponseEntity.ok(responseMapper.toVnPayIpnResponse("99", "System Error"));
        }
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Void> receiveVnPayReturn(
            @RequestParam Map<String, String> queryParams) {
        String orderCode = queryParams.get("vnp_TxnRef");
        String targetFrontend = (frontendUrl != null && !frontendUrl.isBlank()) ? frontendUrl : "http://localhost:3000";

        if (!vnPayCallbackHelper.verifyVnPaySignature(queryParams)) {
            String redirectUrl = targetFrontend + "/settings/billing?status=SIGNATURE_ERROR&orderCode=" + orderCode;
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
        }

        try {
            ConfirmPaymentCommand command = vnPayCallbackHelper.buildVnPayConfirmCommand(queryParams);
            ConfirmPaymentResult result = confirmPaymentInputPort.confirmPayment(command);

            if (command.successful() && vnPayCallbackHelper.isPaymentSuccessfulStatus(result.status())) {
                String redirectUrl = targetFrontend + "/settings/billing?status=PAID&orderCode=" + orderCode;
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
            } else {
                String redirectUrl = targetFrontend + "/settings/billing?status=FAILED&orderCode=" + orderCode;
                return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
            }
        } catch (Exception e) {
            String redirectUrl = targetFrontend + "/settings/billing?status=ERROR&orderCode=" + orderCode;
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(redirectUrl)).build();
        }
    }

    @GetMapping("/{orderCode}")
    @ApiResponseMessage(message = "payment.order.get_success")
    public ResponseEntity<PaymentOrderResponse> getPaymentByOrderCode(
            @PathVariable String orderCode) {
        PaymentOrderResult result = getPaymentInputPort.getPaymentByOrderCode(orderCode);
        PaymentOrderResponse response = responseMapper.resultToOrderResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderCode}/cancel")
    @ApiResponseMessage(message = "payment.order.cancel_success")
    public ResponseEntity<CancelPaymentResponse> cancelPayment(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @PathVariable String orderCode) {
        CancelPaymentCommand command = new CancelPaymentCommand(orderCode, payload.userId());
        CancelPaymentResult result = cancelPaymentInputPort.cancelPayment(command);
        CancelPaymentResponse response = responseMapper.resultToCancelResponse(result);
        return ResponseEntity.ok(response);
    }
}

