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
import org.naho.payment.model.Money;
import org.naho.payment.port.in.CancelPaymentInputPort;
import org.naho.payment.port.in.ConfirmPaymentInputPort;
import org.naho.payment.port.in.CreatePaymentInputPort;
import org.naho.payment.port.in.GetPaymentInputPort;
import org.naho.payment.port.out.PaymentGatewayPort;
import org.naho.payment.port.out.PaymentGatewayResolver;
import org.naho.payment.result.CancelPaymentResult;
import org.naho.payment.result.ConfirmPaymentResult;
import org.naho.payment.result.CreatePaymentResult;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.payment.type.ConfirmPaymentStatus;
import org.naho.payment.type.PaymentProvider;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.annotation.ApiResponseMessage;
import org.naho.shared.exception.ApplicationException;
import org.naho.user.result.AccessTokenPayload;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.HashMap;
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
    private final PaymentGatewayResolver gatewayResolver;

    @PostMapping("/create")
    @ApiResponseMessage(message = "payment.order.creation_success")
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpServletRequest) {
        String clientIp = httpServletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = httpServletRequest.getRemoteAddr();
        }

        CreatePaymentCommand command = new CreatePaymentCommand(
                payload.userId(),
                request.getPlanCode(),
                request.getProvider(),
                clientIp,
                httpServletRequest.getLocale().getLanguage());

        CreatePaymentResult result = createPaymentInputPort.createPayment(command);
        CreatePaymentResponse response = responseMapper.resultToCreateResponse(result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-ipn")
    public ResponseEntity<VnPayIpnResponse> receiveVnPayIpn(
            @RequestParam Map<String, String> queryParams) {
        Map<String, String> fields = new HashMap<>(queryParams);
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        PaymentGatewayPort gateway = gatewayResolver.resolve(PaymentProvider.VNPAY);

        if (vnp_SecureHash == null || gateway.verifySignature(fields, vnp_SecureHash)) {
            return ResponseEntity.ok(responseMapper.toVnPayIpnResponse("97", "Invalid Signature"));
        }

        try {
            ConfirmPaymentCommand command = buildVnPayConfirmCommand(queryParams);
            ConfirmPaymentResult result = confirmPaymentInputPort.confirmPayment(command);

            VnPayIpnResponse response = switch (result.status()) {
                case SUCCESS -> responseMapper.toVnPayIpnResponse("00", "Confirm success");
                case ALREADY_PAID, DUPLICATE -> responseMapper.toVnPayIpnResponse("02", "Order already confirmed");
                default -> responseMapper.toVnPayIpnResponse("99", "Payment transaction failed or error");
            };
            return ResponseEntity.ok(response);

        } catch (ApplicationException e) {
            if ("PAY_A001".equals(e.getErrorCode().getCode())) {
                return ResponseEntity.ok(responseMapper.toVnPayIpnResponse("01", "Order not found"));
            } else if ("PAYMENT_AMOUNT_EMPTY".equals(e.getErrorCode().getCode())
                    || "PAY_004".equals(e.getErrorCode().getCode())) {
                return ResponseEntity.ok(responseMapper.toVnPayIpnResponse("04", "Invalid amount"));
            } else {
                return ResponseEntity.ok(responseMapper.toVnPayIpnResponse("99", "System Error"));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(responseMapper.toVnPayIpnResponse("99", "System Error"));
        }
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<VnPayReturnResponse> receiveVnPayReturn(
            @RequestParam Map<String, String> queryParams) {
        Map<String, String> fields = new HashMap<>(queryParams);
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        PaymentGatewayPort gateway = gatewayResolver.resolve(PaymentProvider.VNPAY);

        if (vnp_SecureHash == null || gateway.verifySignature(fields, vnp_SecureHash)) {
            return ResponseEntity.ok(responseMapper.toVnPayReturnResponse(
                    queryParams.get("vnp_TxnRef"),
                    "SIGNATURE_ERROR",
                    "Chữ ký giao dịch không hợp lệ!"));
        }

        try {
            ConfirmPaymentCommand command = buildVnPayConfirmCommand(queryParams);
            ConfirmPaymentResult result = confirmPaymentInputPort.confirmPayment(command);

            String vnp_TxnRef = queryParams.get("vnp_TxnRef");

            if (command.successful() && (result.status() == ConfirmPaymentStatus.SUCCESS
                    || result.status() == ConfirmPaymentStatus.ALREADY_PAID
                    || result.status() == ConfirmPaymentStatus.DUPLICATE)) {
                return ResponseEntity.ok(responseMapper.toVnPayReturnResponse(
                        vnp_TxnRef,
                        PaymentStatus.PAID.name(),
                        "Thanh toán thành công qua VNPAY!"));
            } else {
                return ResponseEntity.ok(responseMapper.toVnPayReturnResponse(
                        vnp_TxnRef,
                        PaymentStatus.FAILED.name(),
                        "Thanh toán không thành công. Trạng thái: " + result.status()));
            }
        } catch (Exception e) {
            return ResponseEntity.ok(responseMapper.toVnPayReturnResponse(
                    queryParams.get("vnp_TxnRef"),
                    "ERROR",
                    "Lỗi xử lý phản hồi thanh toán: " + e.getMessage()));
        }
    }

    private ConfirmPaymentCommand buildVnPayConfirmCommand(Map<String, String> queryParams) {
        String vnp_TxnRef = queryParams.get("vnp_TxnRef");
        String vnp_TransactionNo = queryParams.get("vnp_TransactionNo");
        String vnp_AmountStr = queryParams.get("vnp_Amount");
        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        String vnp_PayDateStr = queryParams.get("vnp_PayDate");

        BigDecimal amount = (vnp_AmountStr != null && !vnp_AmountStr.isBlank())
                ? new BigDecimal(vnp_AmountStr).divide(new BigDecimal(100))
                : BigDecimal.ZERO;
        Money paidAmount = new Money(amount, Currency.getInstance("VND"));
        boolean successful = "00".equals(vnp_ResponseCode);

        Instant providerTransactionTime;
        try {
            providerTransactionTime = ZonedDateTime.parse(
                    vnp_PayDateStr,
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Ho_Chi_Minh"))).toInstant();
        } catch (Exception e) {
            providerTransactionTime = Instant.now();
        }

        return new ConfirmPaymentCommand(
                PaymentProvider.VNPAY,
                vnp_TxnRef,
                vnp_TransactionNo != null ? vnp_TransactionNo : "VNP_" + vnp_TxnRef,
                paidAmount,
                successful,
                providerTransactionTime,
                queryParams);
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
