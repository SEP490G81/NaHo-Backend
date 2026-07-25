package org.naho.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.payment.type.PaymentProvider;
import org.naho.shared.persistence.BaseEntity;

import java.math.BigDecimal;
import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_transactions")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentTransactionEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_order_id", nullable = false)
    PaymentOrderEntity paymentOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PaymentProvider provider;

    @Column(name = "provider_transaction_id", nullable = false)
    String providerTransactionId;

    @Column(name = "amount_amount", nullable = false)
    BigDecimal amountAmount;

    @Column(name = "amount_currency", nullable = false)
    String amountCurrency;

    @Column(nullable = false)
    Boolean successful;

    @Column(name = "provider_transaction_time")
    Instant providerTransactionTime;

    @Column(columnDefinition = "TEXT")
    String metadata;
}
