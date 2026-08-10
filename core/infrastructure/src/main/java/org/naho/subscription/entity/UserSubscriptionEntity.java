package org.naho.subscription.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.payment.entity.PaymentOrderEntity;
import org.naho.shared.persistence.BaseEntity;
import org.naho.subscription.type.SubscriptionStatus;
import org.naho.user.entity.UserEntity;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_subscriptions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSubscriptionEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    SubscriptionPlanEntity subscriptionPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_order_id", nullable = false)
    PaymentOrderEntity paymentOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    SubscriptionStatus status;

    @Column(name = "start_time", nullable = false)
    Instant startTime;

    @Column(name = "end_time", nullable = false)
    Instant endTime;
}
