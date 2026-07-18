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
public class CreatePaymentResponse {
    private Long paymentOrderId;
    private String orderCode;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentUrl;
    private Instant expiresTime;
}
