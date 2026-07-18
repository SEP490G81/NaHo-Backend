package org.naho.payment.controller.v1;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.naho.payment.command.ConfirmPaymentCommand;
import org.naho.payment.command.CreatePaymentCommand;
import org.naho.payment.dto.mapper.PaymentResponseMapper;
import org.naho.payment.dto.request.CreatePaymentRequest;
import org.naho.payment.dto.response.CreatePaymentResponse;
import org.naho.payment.dto.response.PaymentOrderResponse;
import org.naho.payment.model.Money;
import org.naho.payment.port.in.ConfirmPaymentInputPort;
import org.naho.payment.port.in.CreatePaymentInputPort;
import org.naho.payment.port.in.GetPaymentInputPort;
import org.naho.payment.port.out.PaymentGatewayPort;
import org.naho.payment.port.out.PaymentGatewayResolver;
import org.naho.payment.result.ConfirmPaymentResult;
import org.naho.payment.result.CreatePaymentResult;
import org.naho.payment.result.PaymentOrderResult;
import org.naho.payment.type.PaymentProvider;
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
    private final PaymentResponseMapper responseMapper;
    private final PaymentGatewayResolver gatewayResolver;

    @PostMapping("/create")
    @ApiResponseMessage(message = "payment.order.creation_success")
    public ResponseEntity<CreatePaymentResponse> createPayment(
            @AuthenticationPrincipal AccessTokenPayload payload,
            @RequestBody CreatePaymentRequest request,
            HttpServletRequest httpServletRequest
    ) {
        String clientIp = httpServletRequest.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = httpServletRequest.getRemoteAddr();
        }

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
    public ResponseEntity<Map<String, String>> receiveVnPayIpn(
            @RequestParam Map<String, String> queryParams
    ) {
        Map<String, String> fields = new HashMap<>(queryParams);
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        Map<String, String> response = new HashMap<>();

        PaymentGatewayPort gateway = gatewayResolver.resolve(PaymentProvider.VNPAY);

        if (vnp_SecureHash == null || !gateway.verifySignature(fields, vnp_SecureHash)) {
            response.put("RspCode", "97");
            response.put("Message", "Invalid Signature");
            return ResponseEntity.ok(response);
        }

        try {
            String vnp_TxnRef = queryParams.get("vnp_TxnRef");
            String vnp_TransactionNo = queryParams.get("vnp_TransactionNo");
            String vnp_AmountStr = queryParams.get("vnp_Amount");
            String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
            String vnp_PayDateStr = queryParams.get("vnp_PayDate");

            BigDecimal amount = new BigDecimal(vnp_AmountStr).divide(new BigDecimal(100));
            Money paidAmount = new Money(amount, Currency.getInstance("VND"));
            boolean successful = "00".equals(vnp_ResponseCode);

            Instant providerTransactionTime;
            try {
                providerTransactionTime = ZonedDateTime.parse(
                        vnp_PayDateStr,
                        DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Ho_Chi_Minh"))
                ).toInstant();
            } catch (Exception e) {
                providerTransactionTime = Instant.now();
            }

            ConfirmPaymentCommand command = new ConfirmPaymentCommand(
                    PaymentProvider.VNPAY,
                    vnp_TxnRef,
                    vnp_TransactionNo,
                    paidAmount,
                    successful,
                    providerTransactionTime,
                    queryParams
            );

            ConfirmPaymentResult result = confirmPaymentInputPort.confirmPayment(command);

            switch (result.status()) {
                case "SUCCESS" -> {
                    response.put("RspCode", "00");
                    response.put("Message", "Confirm success");
                }
                case "ALREADY_PAID", "DUPLICATE" -> {
                    response.put("RspCode", "02");
                    response.put("Message", "Order already confirmed");
                }
                default -> {
                    response.put("RspCode", "99");
                    response.put("Message", "Payment transaction failed or error");
                }
            }

        } catch (ApplicationException e) {
            if ("PAY_A001".equals(e.getErrorCode().getCode())) {
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
            } else if ("PAYMENT_AMOUNT_EMPTY".equals(e.getErrorCode().getCode()) || "PAY_004".equals(e.getErrorCode().getCode())) {
                response.put("RspCode", "04");
                response.put("Message", "Invalid amount");
            } else {
                response.put("RspCode", "99");
                response.put("Message", "System Error");
            }
        } catch (Exception e) {
            response.put("RspCode", "99");
            response.put("Message", "System Error");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<Map<String, Object>> receiveVnPayReturn(
            @RequestParam Map<String, String> queryParams
    ) {
        Map<String, String> fields = new HashMap<>(queryParams);
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        Map<String, Object> response = new HashMap<>();

        PaymentGatewayPort gateway = gatewayResolver.resolve(PaymentProvider.VNPAY);

        if (vnp_SecureHash == null || !gateway.verifySignature(fields, vnp_SecureHash)) {
            response.put("status", "SIGNATURE_ERROR");
            response.put("message", "Chữ ký giao dịch không hợp lệ!");
            return ResponseEntity.ok(response);
        }

        String vnp_ResponseCode = queryParams.get("vnp_ResponseCode");
        String vnp_TxnRef = queryParams.get("vnp_TxnRef");

        if ("00".equals(vnp_ResponseCode)) {
            response.put("status", "PAID");
            response.put("orderCode", vnp_TxnRef);
            response.put("message", "Thanh toán thành công qua VNPAY!");
        } else {
            response.put("status", "FAILED");
            response.put("orderCode", vnp_TxnRef);
            response.put("message", "Thanh toán không thành công. Mã lỗi: " + vnp_ResponseCode);
        }

        return ResponseEntity.ok(response);
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
}
