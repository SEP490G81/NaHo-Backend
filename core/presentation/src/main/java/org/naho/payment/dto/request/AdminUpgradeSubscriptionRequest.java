package org.naho.payment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpgradeSubscriptionRequest {

    @NotNull(message = "payment.user_id.empty")
    private Long userId;

    @NotBlank(message = "subscription.plan.code.empty")
    private String planCode;

    private Integer durationDays;
}
