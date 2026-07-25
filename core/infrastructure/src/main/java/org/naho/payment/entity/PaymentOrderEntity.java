package org.naho.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.payment.type.PaymentProvider;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.persistence.BaseEntity;
import org.naho.subscription.entity.SubscriptionPlanEntity;

import java.math.BigDecimal;
import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_orders")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentOrderEntity extends BaseEntity {

    @Column(name = "order_code", nullable = false, unique = true)
    String orderCode;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    SubscriptionPlanEntity subscriptionPlan;

    @Column(name = "amount_amount", nullable = false)
    BigDecimal amountAmount;

    @Column(name = "amount_currency", nullable = false)
    String amountCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentStatus status;

    @Column(name = "provider_transaction_id")
    String providerTransactionId;

    @Column(name = "expires_time", nullable = false)
    Instant expiresTime;

    @Column(name = "paid_time")
    Instant paidTime;
}
