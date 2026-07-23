package org.naho.payment.controller.v1;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.naho.payment.command.CancelPaymentCommand;
import org.naho.payment.command.ConfirmPaymentCommand;
import org.naho.payment.command.CreatePaymentCommand;
import org.naho.payment.dto.mapper.PaymentResponseMapper;
import org.naho.payment.dto.request.CreatePaymentRequest;
import org.naho.payment.dto.response.CancelPaymentResponse;
import org.naho.payment.dto.response.CreatePaymentResponse;
import org.naho.payment.dto.response.PaymentOrderResponse;
import org.naho.payment.dto.response.VnPayIpnResponse;
import org.naho.payment.dto.response.VnPayReturnResponse;
import org.naho.payment.helper.VnPayCallbackHelper;
import org.naho.payment.port.in.CancelPaymentInputPort;
import org.naho.payment.port.in.ConfirmPaymentInputPort;
import org.naho.payment.port.in.CreatePaymentInputPort;
import org.naho.payment.port.in.GetPaymentInputPort;
import org.naho.payment.result.CancelPaymentResult;
import org.naho.payment.result.ConfirmPaymentResult;
import org.naho.payment.result.CreatePaymentResult;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final CreatePaymentInputPort createPaymentInputPort;
    private final ConfirmPaymentInputPort confirmPaymentInputPort;
    private final GetPaymentInputPort getPaymentInputPort;
    private final CancelPaymentInputPort cancelPaymentInputPort;
    private final PaymentResponseMapper responseMapper;
    private final VnPayCallbackHelper vnPayCallbackHelper;

    @PostMapping("/create")
    @ApiResponseMessage(message = "payment.order.creation_success")
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String clientIp = vnPayCallbackHelper.extractClientIp(httpServletRequest);

        CreatePaymentCommand command = new CreatePaymentCommand(
                payload.userId(),
                request.getPlanCode(),
                request.getProvider(),
                clientIp,
                httpServletRequest.getLocale().getLanguage()
        );

        CreatePaymentResult result = createPaymentInputPort.createPayment(command);
        CreatePaymentResponse response = responseMapper.resultToCreateResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<VnPayIpnResponse> receiveVnPayIpn(
            @RequestParam Map<String, String> queryParams
    ) {
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
    public ResponseEntity<VnPayReturnResponse> receiveVnPayReturn(
            @RequestParam Map<String, String> queryParams
    ) {
        String orderCode = queryParams.get("vnp_TxnRef");

        if (!vnPayCallbackHelper.verifyVnPaySignature(queryParams)) {
            return ResponseEntity.ok(responseMapper.toVnPayReturnResponse(
                    orderCode,
                    "SIGNATURE_ERROR",
                    "Chữ ký giao dịch không hợp lệ!"
            ));
        }

        try {
            ConfirmPaymentCommand command = vnPayCallbackHelper.buildVnPayConfirmCommand(queryParams);
            ConfirmPaymentResult result = confirmPaymentInputPort.confirmPayment(command);

            if (command.successful() && vnPayCallbackHelper.isPaymentSuccessfulStatus(result.status())) {
                return ResponseEntity.ok(responseMapper.toVnPayReturnResponse(
                        orderCode,
                        PaymentStatus.PAID.name(),
                        "Thanh toán thành công qua VNPAY!"
                ));
            } else {
                return ResponseEntity.ok(responseMapper.toVnPayReturnResponse(
                        orderCode,
                        PaymentStatus.FAILED.name(),
                        "Thanh toán không thành công. Trạng thái: " + result.status()
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(responseMapper.toVnPayReturnResponse(
                    orderCode,
                    "ERROR",
                    "Lỗi xử lý phản hồi thanh toán: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{orderCode}")
    @ApiResponseMessage(message = "payment.order.get_success")
    public ResponseEntity<PaymentOrderResponse> getPaymentByOrderCode(
            @PathVariable String orderCode
    ) {
        PaymentOrderResult result = getPaymentInputPort.getPaymentByOrderCode(orderCode);
        PaymentOrderResponse response = responseMapper.resultToOrderResponse(result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{orderCode}/cancel")
    @ApiResponseMessage(message = "payment.order.cancel_success")
    public ResponseEntity<CancelPaymentResponse> cancelPayment(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @PathVariable String orderCode
    ) {
        CancelPaymentCommand command = new CancelPaymentCommand(orderCode, payload.userId());
        CancelPaymentResult result = cancelPaymentInputPort.cancelPayment(command);
        CancelPaymentResponse response = responseMapper.resultToCancelResponse(result);
        return ResponseEntity.ok(response);
    }
}
