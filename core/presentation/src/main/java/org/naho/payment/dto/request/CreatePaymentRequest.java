package org.naho.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.naho.payment.type.PaymentProvider;
import org.naho.subscription.type.PlanCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private PlanCode planCode;
    private PaymentProvider provider;
}
