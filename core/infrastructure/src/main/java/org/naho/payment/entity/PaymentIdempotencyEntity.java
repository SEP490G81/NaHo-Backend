package org.naho.payment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.naho.shared.persistence.BaseEntity;

import java.time.Instant;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payment_idempotencies", uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_idempotency_user_key", columnNames = {"user_id",
                "idempotency_key"})
})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaymentIdempotencyEntity extends BaseEntity {

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "idempotency_key", nullable = false, length = 100)
    String idempotencyKey;

    @Column(name = "request_hash", nullable = false, length = 64)
    String requestHash;

    @Column(name = "payment_order_id", nullable = false)
    Long paymentOrderId;

    @Column(name = "retention_expires_time", nullable = false)
    Instant retentionExpiresTime;
}
