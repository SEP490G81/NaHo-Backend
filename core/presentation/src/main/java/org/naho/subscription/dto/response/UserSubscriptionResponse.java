package org.naho.subscription.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSubscriptionResponse {
    private Long id;
    private Long userId;
    private Long subscriptionPlanId;
    private Long paymentOrderId;
    private String status;
    private Instant startTime;
    private Instant endTime;
    private Instant createdTime;
    private Instant modifiedTime;
}
