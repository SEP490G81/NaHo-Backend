package org.naho.payment.model;

import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.exception.PaymentDomainErrorCode;
import org.naho.payment.type.PaymentProvider;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.exception.DomainException;

import java.time.Instant;
import java.util.Objects;

public class PaymentOrder {
    private final Long id;
    private final String orderCode;
    private final Long userId;
    private final Long subscriptionPlanId;
    private final Money amount;
    private PaymentProvider provider;
    private PaymentStatus status;
    private String providerTransactionId;
    private final Instant createdTime; // Thời gian tạo hóa đơn
    private final Instant expiresTime; // Thời gian hết hạn thanh toán
    private Instant paidTime; //Thời gian xác nhận đã thanh toán thành công
    private Instant modifiedTime;// Thời gian cập nhật hóa đơn

    private PaymentOrder(Builder builder) {
        this.id = builder.id;
        this.orderCode = builder.orderCode;
        this.userId = builder.userId;
        this.subscriptionPlanId = builder.subscriptionPlanId;
        this.amount = builder.amount;
        this.provider = builder.provider;
        this.status = builder.status;
        this.providerTransactionId = builder.providerTransactionId;
        this.createdTime = builder.createdTime;
        this.expiresTime = builder.expiresTime;
        this.paidTime = builder.paidTime;
        this.modifiedTime = builder.modifiedTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static PaymentOrder create(
            String orderCode,
            Long userId,
            Long subscriptionPlanId,
            Money amount,
            Instant now,
            Instant expiresTime
    ) {
        return builder()
                .orderCode(orderCode)
                .userId(userId)
                .subscriptionPlanId(subscriptionPlanId)
                .amount(amount)
                .provider(PaymentProvider.UNASSIGNED)
                .status(PaymentStatus.PENDING)
                .createdTime(now)
                .expiresTime(expiresTime)
                .build();
    }

    public void assignProvider(PaymentProvider provider, Instant now) {
        if (status != PaymentStatus.PENDING) {
            throw new DomainException(
                    PaymentDomainErrorCode.PAYMENT_INVALID_STATE,
                    PaymentDetailMessageKey.PAYMENT_INVALID_STATE
            );
        }
        this.provider = Objects.requireNonNull(provider);
        this.modifiedTime = now;
    }

    public void markProcessing(Instant now) {
        ensurePending();
        ensureNotExpired(now);
        this.status = PaymentStatus.PROCESSING;
        this.modifiedTime = now;
    }

    public void markPaid(String providerTransactionId, Money paidAmount, Instant now) {
        if (status == PaymentStatus.PAID) {
            return;
        }

        if (status != PaymentStatus.PENDING && status != PaymentStatus.PROCESSING) {
            throw new DomainException(
                    PaymentDomainErrorCode.PAYMENT_INVALID_STATE,
                    PaymentDetailMessageKey.PAYMENT_INVALID_STATE
            );
        }

        ensureNotExpired(now);

        if (!amount.hasSameValue(paidAmount)) {
            throw new DomainException(
                    PaymentDomainErrorCode.PAYMENT_AMOUNT_EMPTY,
                    PaymentDetailMessageKey.PAYMENT_AMOUNT_EMPTY
            );
        }

        this.providerTransactionId = Objects.requireNonNull(providerTransactionId);
        this.status = PaymentStatus.PAID;
        this.paidTime = now;
        this.modifiedTime = now;
    }

    public void markFailed(Instant now) {
        if (status == PaymentStatus.PAID) {
            throw new DomainException(
                    PaymentDomainErrorCode.PAYMENT_INVALID_STATE,
                    PaymentDetailMessageKey.PAYMENT_INVALID_STATE
            );
        }
        this.status = PaymentStatus.FAILED;
        this.modifiedTime = now;
    }

    public void expire(Instant now) {
        if (status == PaymentStatus.PAID) {
            return;
        }
        if (!now.isBefore(expiresTime)) {
            this.status = PaymentStatus.EXPIRED;
            this.modifiedTime = now;
        }
    }

    public void cancel(Instant now) {
        ensurePending();
        this.status = PaymentStatus.CANCELLED;
        this.modifiedTime = now;
    }

    public boolean isPaid() {
        return status == PaymentStatus.PAID;
    }

    public boolean isExpiredAt(Instant now) {
        return !now.isBefore(expiresTime);
    }

    private void ensurePending() {
        if (status != PaymentStatus.PENDING) {
            throw new DomainException(
                    PaymentDomainErrorCode.PAYMENT_INVALID_STATE,
                    PaymentDetailMessageKey.PAYMENT_INVALID_STATE
            );
        }
    }

    private void ensureNotExpired(Instant now) {
        if (isExpiredAt(now)) {
            throw new DomainException(
                    PaymentDomainErrorCode.PAYMENT_EXPIRATION_INVALID,
                    PaymentDetailMessageKey.PAYMENT_EXPIRATION_INVALID
            );
        }
    }

    // Getters
    public Long getId() { return id; }
    public String getOrderCode() { return orderCode; }
    public Long getUserId() { return userId; }
    public Long getSubscriptionPlanId() { return subscriptionPlanId; }
    public Money getAmount() { return amount; }
    public PaymentProvider getProvider() { return provider; }
    public PaymentStatus getStatus() { return status; }
    public String getProviderTransactionId() { return providerTransactionId; }
    public Instant getCreatedTime() { return createdTime; }
    public Instant getExpiresTime() { return expiresTime; }
    public Instant getPaidTime() { return paidTime; }
    public Instant getModifiedTime() { return modifiedTime; }

    public static final class Builder {
        private Long id;
        private String orderCode;
        private Long userId;
        private Long subscriptionPlanId;
        private Money amount;
        private PaymentProvider provider;
        private PaymentStatus status;
        private String providerTransactionId;
        private Instant createdTime;
        private Instant expiresTime;
        private Instant paidTime;
        private Instant modifiedTime;

        private Builder() {}

        public Builder id(Long id) { this.id = id; return this; }
        public Builder orderCode(String orderCode) { this.orderCode = orderCode; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder subscriptionPlanId(Long subscriptionPlanId) { this.subscriptionPlanId = subscriptionPlanId; return this; }
        public Builder amount(Money amount) { this.amount = amount; return this; }
        public Builder provider(PaymentProvider provider) { this.provider = provider; return this; }
        public Builder status(PaymentStatus status) { this.status = status; return this; }
        public Builder providerTransactionId(String providerTransactionId) { this.providerTransactionId = providerTransactionId; return this; }
        public Builder createdTime(Instant createdTime) { this.createdTime = createdTime; return this; }
        public Builder expiresTime(Instant expiresTime) { this.expiresTime = expiresTime; return this; }
        public Builder paidTime(Instant paidTime) { this.paidTime = paidTime; return this; }
        public Builder modifiedTime(Instant modifiedTime) { this.modifiedTime = modifiedTime; return this; }

        public PaymentOrder build() {
            if (orderCode == null || orderCode.isBlank()) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_ORDER_CODE_EMPTY, PaymentDetailMessageKey.PAYMENT_ORDER_CODE_EMPTY);
            }
            if (userId == null) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_USER_ID_EMPTY, PaymentDetailMessageKey.PAYMENT_USER_ID_EMPTY);
            }
            if (subscriptionPlanId == null) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_PLAN_ID_EMPTY, PaymentDetailMessageKey.PAYMENT_PLAN_ID_EMPTY);
            }
            if (amount == null) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_AMOUNT_EMPTY, PaymentDetailMessageKey.PAYMENT_AMOUNT_EMPTY);
            }
            if (createdTime == null) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_EXPIRATION_INVALID, PaymentDetailMessageKey.PAYMENT_EXPIRATION_INVALID);
            }
            if (expiresTime == null || !expiresTime.isAfter(createdTime)) {
                throw new DomainException(PaymentDomainErrorCode.PAYMENT_EXPIRATION_INVALID, PaymentDetailMessageKey.PAYMENT_EXPIRATION_INVALID);
            }
            if (provider == null) {
                provider = PaymentProvider.UNASSIGNED;
            }
            if (status == null) {
                status = PaymentStatus.PENDING;
            }
            return new PaymentOrder(this);
        }
    }
}
