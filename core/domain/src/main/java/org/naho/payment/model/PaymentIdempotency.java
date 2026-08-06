package org.naho.payment.model;

import java.time.Instant;
import java.util.Objects;

public class PaymentIdempotency {
    private final Long id;
    private final Long userId;
    private final String idempotencyKey;
    private final String requestHash;
    private final Long paymentOrderId;
    private final Instant createdTime;
    private final Instant retentionExpiresTime;

    private PaymentIdempotency(Builder builder) {
        this.id = builder.id;
        this.userId = Objects.requireNonNull(builder.userId, "userId must not be null");
        this.idempotencyKey = Objects.requireNonNull(builder.idempotencyKey, "idempotencyKey must not be null");
        this.requestHash = Objects.requireNonNull(builder.requestHash, "requestHash must not be null");
        this.paymentOrderId = Objects.requireNonNull(builder.paymentOrderId, "paymentOrderId must not be null");
        this.createdTime = Objects.requireNonNull(builder.createdTime, "createdTime must not be null");
        this.retentionExpiresTime = Objects.requireNonNull(builder.retentionExpiresTime,
                "retentionExpiresTime must not be null");
    }

    public static PaymentIdempotency create(
            Long userId,
            String idempotencyKey,
            String requestHash,
            Long paymentOrderId,
            Instant createdTime,
            Instant retentionExpiresTime) {
        return builder()
                .userId(userId)
                .idempotencyKey(idempotencyKey)
                .requestHash(requestHash)
                .paymentOrderId(paymentOrderId)
                .createdTime(createdTime)
                .retentionExpiresTime(retentionExpiresTime)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean matchesRequestHash(String hash) {
        return this.requestHash.equals(hash);
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getRequestHash() {
        return requestHash;
    }

    public Long getPaymentOrderId() {
        return paymentOrderId;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public Instant getRetentionExpiresTime() {
        return retentionExpiresTime;
    }

    public static final class Builder {
        private Long id;
        private Long userId;
        private String idempotencyKey;
        private String requestHash;
        private Long paymentOrderId;
        private Instant createdTime;
        private Instant retentionExpiresTime;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder requestHash(String requestHash) {
            this.requestHash = requestHash;
            return this;
        }

        public Builder paymentOrderId(Long paymentOrderId) {
            this.paymentOrderId = paymentOrderId;
            return this;
        }

        public Builder createdTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder retentionExpiresTime(Instant retentionExpiresTime) {
            this.retentionExpiresTime = retentionExpiresTime;
            return this;
        }

        public PaymentIdempotency build() {
            return new PaymentIdempotency(this);
        }
    }
}
