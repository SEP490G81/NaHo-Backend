package org.naho.payment.helper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.naho.payment.command.ConfirmPaymentCommand;
import org.naho.payment.dto.mapper.PaymentResponseMapper;
import org.naho.payment.dto.response.VnPayIpnResponse;
import org.naho.payment.model.Money;
import org.naho.payment.port.out.PaymentGatewayPort;
import org.naho.payment.port.out.PaymentGatewayResolver;
import org.naho.payment.type.ConfirmPaymentStatus;
import org.naho.payment.type.PaymentProvider;
import org.naho.shared.exception.ApplicationException;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class VnPayCallbackHelper {

    private final PaymentGatewayResolver gatewayResolver;
    private final PaymentResponseMapper responseMapper;

    public boolean verifyVnPaySignature(Map<String, String> queryParams) {
        Map<String, String> fields = new HashMap<>(queryParams);
        String vnp_SecureHash = fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        PaymentGatewayPort gateway = gatewayResolver.resolve(PaymentProvider.VNPAY);
        return vnp_SecureHash != null && gateway.verifySignature(fields, vnp_SecureHash);
    }

    public VnPayIpnResponse mapIpnStatusToResponse(ConfirmPaymentStatus status) {
        return switch (status) {
            case SUCCESS -> responseMapper.toVnPayIpnResponse("00", "Confirm success");
            case ALREADY_PAID, DUPLICATE -> responseMapper.toVnPayIpnResponse("02", "Order already confirmed");
            default -> responseMapper.toVnPayIpnResponse("99", "Payment transaction failed or error");
        };
    }

    public VnPayIpnResponse mapIpnApplicationExceptionToResponse(ApplicationException e) {
        String code = e.getErrorCode().getCode();
        if ("PAY_A001".equals(code)) {
            return responseMapper.toVnPayIpnResponse("01", "Order not found");
        } else if ("PAYMENT_AMOUNT_EMPTY".equals(code) || "PAY_004".equals(code)) {
            return responseMapper.toVnPayIpnResponse("04", "Invalid amount");
        } else {
            return responseMapper.toVnPayIpnResponse("99", "System Error");
        }
    }

    public boolean isPaymentSuccessfulStatus(ConfirmPaymentStatus status) {
        return status == ConfirmPaymentStatus.SUCCESS
                || status == ConfirmPaymentStatus.ALREADY_PAID
                || status == ConfirmPaymentStatus.DUPLICATE;
    }

    public String extractClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

    public ConfirmPaymentCommand buildVnPayConfirmCommand(Map<String, String> queryParams) {
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
                    DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Ho_Chi_Minh"))
            ).toInstant();
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
                queryParams
        );
    }
}
