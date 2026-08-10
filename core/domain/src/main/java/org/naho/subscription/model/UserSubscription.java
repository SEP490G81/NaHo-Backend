package org.naho.subscription.model;

import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.exception.DomainException;
import org.naho.subscription.exception.SubscriptionDomainErrorCode;
import org.naho.subscription.type.SubscriptionStatus;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class UserSubscription {
    private final Long id;
    private final Long userId;
    private final Long subscriptionPlanId;
    private final Long paymentOrderId;
    private final Instant startTime;
    private final Instant endTime;
    private SubscriptionStatus status;

    private UserSubscription(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.subscriptionPlanId = builder.subscriptionPlanId;
        this.paymentOrderId = builder.paymentOrderId;
        this.status = builder.status;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static UserSubscription activate(
            Long userId,
            Long planId,
            Long paymentOrderId,
            Integer durationDays,
            Instant now) {
        Instant endTime = (durationDays != null) ? now.plus(durationDays, ChronoUnit.DAYS) : null;
        return builder()
                .userId(userId)
                .subscriptionPlanId(planId)
                .paymentOrderId(paymentOrderId)
                .status(SubscriptionStatus.ACTIVE)
                .startTime(now)
                .endTime(endTime)
                .build();
    }

    public boolean isActiveAt(Instant now) {
        return status == SubscriptionStatus.ACTIVE
                && !now.isBefore(startTime)
                && (endTime == null || now.isBefore(endTime));
    }

    public void expire(Instant now) {
        if (endTime != null && !now.isBefore(endTime)) {
            status = SubscriptionStatus.EXPIRED;
        }
    }

    public void cancel(Instant now) {
        this.status = SubscriptionStatus.CANCELLED;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public Long getPaymentOrderId() {
        return paymentOrderId;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public static final class Builder {
        private Long id;
        private Long userId;
        private Long subscriptionPlanId;
        private Long paymentOrderId;
        private SubscriptionStatus status;
        private Instant startTime;
        private Instant endTime;

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

        public Builder subscriptionPlanId(Long subscriptionPlanId) {
            this.subscriptionPlanId = subscriptionPlanId;
            return this;
        }

        public Builder paymentOrderId(Long paymentOrderId) {
            this.paymentOrderId = paymentOrderId;
            return this;
        }

        public Builder status(SubscriptionStatus status) {
            this.status = status;
            return this;
        }

        public Builder startTime(Instant startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(Instant endTime) {
            this.endTime = endTime;
            return this;
        }

        public UserSubscription build() {
            if (userId == null) {
                throw new DomainException(SubscriptionDomainErrorCode.SUBSCRIPTION_USER_ID_EMPTY,
                        SubscriptionDetailMessageKey.SUBSCRIPTION_USER_ID_EMPTY);
            }
            if (subscriptionPlanId == null) {
                throw new DomainException(SubscriptionDomainErrorCode.SUBSCRIPTION_PLAN_ID_EMPTY,
                        SubscriptionDetailMessageKey.SUBSCRIPTION_PLAN_ID_EMPTY);
            }
            if (status == null) {
                throw new DomainException(SubscriptionDomainErrorCode.SUBSCRIPTION_STATUS_EMPTY,
                        SubscriptionDetailMessageKey.SUBSCRIPTION_STATUS_EMPTY);
            }
            if (startTime == null) {
                throw new DomainException(SubscriptionDomainErrorCode.SUBSCRIPTION_START_TIME_EMPTY,
                        SubscriptionDetailMessageKey.SUBSCRIPTION_START_TIME_EMPTY);
            }
            if (endTime == null) {
                throw new DomainException(SubscriptionDomainErrorCode.SUBSCRIPTION_END_TIME_EMPTY,
                        SubscriptionDetailMessageKey.SUBSCRIPTION_END_TIME_EMPTY);
            }
            if (startTime.isAfter(endTime)) {
                throw new DomainException(SubscriptionDomainErrorCode.SUBSCRIPTION_TIME_INVALID,
                        SubscriptionDetailMessageKey.SUBSCRIPTION_TIME_INVALID);
            }

            return new UserSubscription(this);
        }
    }
}
