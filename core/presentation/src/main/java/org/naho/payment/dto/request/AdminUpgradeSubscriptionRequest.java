package org.naho.payment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.naho.subscription.type.PlanCode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpgradeSubscriptionRequest {

    @NotNull(message = "payment.user_id.empty")
    private Long userId;

    @NotNull(message = "subscription.plan.code.empty")
    private PlanCode planCode;

    private Integer durationDays;
}
