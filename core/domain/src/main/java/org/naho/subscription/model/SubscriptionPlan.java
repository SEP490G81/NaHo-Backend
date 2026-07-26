package org.naho.subscription.model;

import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.payment.model.Money;
import org.naho.shared.exception.DomainException;
import org.naho.subscription.exception.SubscriptionDomainErrorCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;

import java.time.Instant;

public class SubscriptionPlan {
    private final Long id;
    private final String code;
    private String name;
    private String description;
    private PlanTier tier;
    private Money price;
    private int durationDays;
    private UsageQuota quota;
    private boolean fullCurriculumAccess;
    private boolean progressAnalyticsEnabled;
    private boolean sampleAnswerEnabled;
    private PlanStatus status;
    private final Instant createdTime;
    private Instant modifiedTime;

    private SubscriptionPlan(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.name = builder.name;
        this.description = builder.description;
        this.tier = builder.tier;
        this.price = builder.price;
        this.durationDays = builder.durationDays;
        this.quota = builder.quota;
        this.fullCurriculumAccess = builder.fullCurriculumAccess;
        this.progressAnalyticsEnabled = builder.progressAnalyticsEnabled;
        this.sampleAnswerEnabled = builder.sampleAnswerEnabled;
        this.status = builder.status;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SubscriptionPlan create(
            String code,
            String name,
            String description,
            PlanTier tier,
            Money price,
            int durationDays,
            UsageQuota quota,
            boolean fullCurriculumAccess,
            boolean progressAnalyticsEnabled,
            boolean sampleAnswerEnabled,
            Instant now) {
        return builder()
                .code(code)
                .name(name)
                .description(description)
                .tier(tier)
                .price(price)
                .durationDays(durationDays)
                .quota(quota)
                .fullCurriculumAccess(fullCurriculumAccess)
                .progressAnalyticsEnabled(progressAnalyticsEnabled)
                .sampleAnswerEnabled(sampleAnswerEnabled)
                .status(PlanStatus.ACTIVE)
                .createdTime(now)
                .build();
    }

    public boolean isAvailableForPurchase() {
        return status == PlanStatus.ACTIVE;
    }

    public boolean isFree() {
        return price.isZero();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PlanTier getTier() {
        return tier;
    }

    public Money getPrice() {
        return price;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public UsageQuota getQuota() {
        return quota;
    }

    public boolean isFullCurriculumAccess() {
        return fullCurriculumAccess;
    }

    public boolean isProgressAnalyticsEnabled() {
        return progressAnalyticsEnabled;
    }

    public boolean isSampleAnswerEnabled() {
        return sampleAnswerEnabled;
    }

    public PlanStatus getStatus() {
        return status;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public Instant getModifiedTime() {
        return modifiedTime;
    }

    public static final class Builder {
        private Long id;
        private String code;
        private String name;
        private String description;
        private PlanTier tier;
        private Money price;
        private int durationDays;
        private UsageQuota quota;
        private boolean fullCurriculumAccess;
        private boolean progressAnalyticsEnabled;
        private boolean sampleAnswerEnabled;
        private PlanStatus status;
        private Instant createdTime;
        private Instant modifiedTime;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder tier(PlanTier tier) {
            this.tier = tier;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder durationDays(int durationDays) {
            this.durationDays = durationDays;
            return this;
        }

        public Builder quota(UsageQuota quota) {
            this.quota = quota;
            return this;
        }

        public Builder fullCurriculumAccess(boolean fullCurriculumAccess) {
            this.fullCurriculumAccess = fullCurriculumAccess;
            return this;
        }

        public Builder progressAnalyticsEnabled(boolean progressAnalyticsEnabled) {
            this.progressAnalyticsEnabled = progressAnalyticsEnabled;
            return this;
        }

        public Builder sampleAnswerEnabled(boolean sampleAnswerEnabled) {
            this.sampleAnswerEnabled = sampleAnswerEnabled;
            return this;
        }

        public Builder status(PlanStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime(Instant modifiedTime) {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public SubscriptionPlan build() {
            if (code == null || code.isBlank()) {
                throw new DomainException(SubscriptionDomainErrorCode.PLAN_CODE_EMPTY,
                        SubscriptionDetailMessageKey.PLAN_CODE_EMPTY);
            }
            if (name == null || name.isBlank()) {
                throw new DomainException(SubscriptionDomainErrorCode.PLAN_NAME_EMPTY,
                        SubscriptionDetailMessageKey.PLAN_NAME_EMPTY);
            }
            if (price == null) {
                throw new DomainException(SubscriptionDomainErrorCode.PLAN_PRICE_EMPTY,
                        SubscriptionDetailMessageKey.PLAN_PRICE_EMPTY);
            }
            if (durationDays <= 0) {
                throw new DomainException(SubscriptionDomainErrorCode.PLAN_DURATION_INVALID,
                        SubscriptionDetailMessageKey.PLAN_DURATION_INVALID);
            }
            if (quota == null) {
                throw new DomainException(SubscriptionDomainErrorCode.PLAN_QUOTA_EMPTY,
                        SubscriptionDetailMessageKey.PLAN_QUOTA_EMPTY);
            }
            if (tier == null) {
                throw new DomainException(SubscriptionDomainErrorCode.PLAN_TIER_EMPTY,
                        SubscriptionDetailMessageKey.PLAN_TIER_EMPTY);
            }
            if (status == null) {
                status = PlanStatus.ACTIVE;
            }
            if (createdTime == null) {
                createdTime = Instant.now();
            }
            return new SubscriptionPlan(this);
        }
    }
}
