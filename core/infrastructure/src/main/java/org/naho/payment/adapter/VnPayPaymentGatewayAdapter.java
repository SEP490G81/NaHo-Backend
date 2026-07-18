package org.naho.payment.adapter;

import org.naho.payment.model.PaymentOrder;
import org.naho.payment.port.out.PaymentGatewayPort;
import org.naho.payment.type.PaymentProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Override
    public PaymentProvider supportedProvider() {
        return PaymentProvider.VNPAY;
    }

    @Override
    public PaymentInitializationResult initialize(PaymentOrder paymentOrder, PaymentCustomerContext customerContext) {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String vnp_OrderType = "other";
        
        long amountInCents = paymentOrder.getAmount().amount().multiply(new java.math.BigDecimal(100)).longValue();
        String vnp_Amount = String.valueOf(amountInCents);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        ZonedDateTime nowJp = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        String vnp_CreateDate = nowJp.format(formatter);
        String vnp_TxnRef = paymentOrder.getOrderCode();
        String vnp_OrderInfo = "Thanh toan don hang " + paymentOrder.getOrderCode();
        
        String locale = customerContext.locale();
        String vnp_Locale = (locale != null && (locale.equalsIgnoreCase("en") || locale.equalsIgnoreCase("us"))) ? "en" : "vn";
        
        String clientIp = customerContext.clientIp();
        String vnp_IpAddr = (clientIp != null && !clientIp.isBlank()) ? clientIp : "127.0.0.1";

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", vnp_Amount);
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", vnp_OrderType);
        vnp_Params.put("vnp_Locale", vnp_Locale);
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

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
        String vnp_SecureHash = hmacSHA512(hashSecret, hashData);
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

        URI paymentUri = URI.create(paymentUrl + "?" + queryUrl);
        Instant expiresTime = paymentOrder.getExpiresTime();

        return new PaymentInitializationResult(paymentUri, expiresTime, Map.of());
    }

    public boolean verifySignature(Map<String, String> fields, String secureHash) {
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        
        List<String> pairs = new ArrayList<>();
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                pairs.add(fieldName + "=" + encode(fieldValue));
            }
        }
        
        String calculatedHash = hmacSHA512(hashSecret, String.join("&", pairs));
        return calculatedHash.equalsIgnoreCase(secureHash);
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString()).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }

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
}
