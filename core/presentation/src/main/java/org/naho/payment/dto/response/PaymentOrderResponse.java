package org.naho.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderResponse {
    private Long id;
    private String orderCode;
    private Long userId;
    private Long subscriptionPlanId;
    private BigDecimal amount;
    private String currency;
    private String provider;
    private String status;
    private String providerTransactionId;
    private Instant createdTime;
    private Instant expiresTime;
    private Instant paidTime;
    private Instant modifiedTime;
}
