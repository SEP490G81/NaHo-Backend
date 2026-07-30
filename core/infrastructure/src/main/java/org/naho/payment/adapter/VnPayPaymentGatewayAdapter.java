package org.naho.payment.adapter;

import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.out.PaymentGatewayPort;
import org.naho.payment.type.PaymentProvider;
import org.naho.shared.constant.SystemZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
public class VnPayPaymentGatewayAdapter implements PaymentGatewayPort {

    @Value("${app.vnpay.tmn-code}")
    private String tmnCode;

    @Value("${app.vnpay.hash-secret}")
    private String hashSecret;

    @Value("${app.vnpay.payment-url}")
    private String paymentUrl;

    @Value("${app.vnpay.return-url}")
    private String returnUrl;

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                return null;
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public PaymentProvider supportedProvider() {
        return PaymentProvider.VNPAY;
    }

    @Override
    public PaymentInitializationResult initialize(PaymentOrder paymentOrder, PaymentCustomerContext customerContext) {
        String normalizedTmnCode = normalizeRequiredConfig("app.vnpay.tmn-code", tmnCode);
        String normalizedHashSecret = normalizeRequiredConfig("app.vnpay.hash-secret", hashSecret);
        String normalizedPaymentUrl = normalizeRequiredConfig("app.vnpay.payment-url", paymentUrl);
        String normalizedReturnUrl = normalizeRequiredConfig("app.vnpay.return-url", returnUrl);

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderType = "other";

        long amountInCents = paymentOrder.getAmount().amount().multiply(new java.math.BigDecimal(100)).longValue();
        String vnp_Amount = String.valueOf(amountInCents);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        ZonedDateTime nowVn = ZonedDateTime.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
        String vnp_CreateDate = nowVn.format(formatter);
        ZonedDateTime expireVn = ZonedDateTime.ofInstant(paymentOrder.getExpiresTime(), SystemZoneId.HO_CHI_MINH_ZONE_ID);
        String vnp_ExpireDate = expireVn.format(formatter);

        String vnp_TxnRef = paymentOrder.getOrderCode();
        String vnp_OrderInfo = "Thanh toan don hang " + paymentOrder.getOrderCode();

        String locale = customerContext.locale();
        String vnp_Locale = (locale != null && (locale.equalsIgnoreCase("en") || locale.equalsIgnoreCase("us"))) ? "en" : "vn";

        String clientIp = customerContext.clientIp();
        String vnp_IpAddr = (clientIp != null && !clientIp.isBlank()) ? clientIp : "127.0.0.1";
        if (vnp_IpAddr.contains(":") || vnp_IpAddr.equals("0:0:0:0:0:0:0:1") || vnp_IpAddr.equals("::1")) {
            vnp_IpAddr = "127.0.0.1";
        }

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", normalizedTmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", normalizedReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        List<String> hashPairs = new ArrayList<>();
        List<String> queryPairs = new ArrayList<>();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashPairs.add(fieldName + "=" + encode(fieldValue));
                queryPairs.add(encode(fieldName) + "=" + encode(fieldValue));
            }
        }

        String queryUrl = String.join("&", queryPairs);
        String hashData = String.join("&", hashPairs);
        String vnp_SecureHash = hmacSHA512(normalizedHashSecret, hashData);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        URI paymentUri = URI.create(normalizedPaymentUrl + "?" + queryUrl);
        Instant expiresTime = paymentOrder.getExpiresTime();

        return new PaymentInitializationResult(paymentUri, expiresTime, Map.of());
    }

    public boolean verifySignature(Map<String, String> fields, String secureHash) {
        if (fields == null || secureHash == null || secureHash.isBlank()) {
            return false;
        }

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);

        List<String> pairs = new ArrayList<>();
        for (String fieldName : fieldNames) {
            if (!fieldName.startsWith("vnp_")
                    || fieldName.equals("vnp_SecureHash")
                    || fieldName.equals("vnp_SecureHashType")) {
                continue;
            }
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                pairs.add(fieldName + "=" + encode(fieldValue));
            }
        }

        String normalizedHashSecret = normalizeRequiredConfig("app.vnpay.hash-secret", hashSecret);
        String calculatedHash = hmacSHA512(normalizedHashSecret, String.join("&", pairs));

        return MessageDigest.isEqual(
                calculatedHash.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII),
                secureHash.toLowerCase(Locale.ROOT).getBytes(StandardCharsets.US_ASCII)
        );
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.US_ASCII);
    }

    private String normalizeRequiredConfig(String propertyName, String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(propertyName + " must not be blank");
        }

        return value.strip();
    }
}