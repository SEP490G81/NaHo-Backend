package org.naho.subscription.model;

import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.payment.model.Money;
import org.naho.shared.exception.DomainException;
import org.naho.subscription.exception.SubscriptionDomainErrorCode;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.PlanTier;

public class SubscriptionPlan {
    private final Long id;
    private final PlanCode code;
    private String name;
    private String description;
    private PlanTier tier;
    private Money price;
    private Integer durationDays;
    private UsageQuota quota;
    private Boolean fullCurriculumAccess;
    private Boolean progressAnalyticsEnabled;
    private Boolean sampleAnswerEnabled;
    private Double maxAnswerTimeSeconds;
    private Boolean saveAnswerHistoryEnabled;
    private PlanStatus status;

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
        this.maxAnswerTimeSeconds = builder.maxAnswerTimeSeconds;
        this.saveAnswerHistoryEnabled = builder.saveAnswerHistoryEnabled;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SubscriptionPlan create(
            PlanCode code,
            String name,
            String description,
            PlanTier tier,
            Money price,
            Integer durationDays,
            UsageQuota quota,
            Boolean fullCurriculumAccess,
            Boolean progressAnalyticsEnabled,
            Boolean sampleAnswerEnabled,
            Double maxAnswerTimeSeconds,
            Boolean saveAnswerHistoryEnabled) {
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
                .maxAnswerTimeSeconds(maxAnswerTimeSeconds)
                .saveAnswerHistoryEnabled(saveAnswerHistoryEnabled)
                .status(PlanStatus.ACTIVE)
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

    public PlanCode getCode() {
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

    public Integer getDurationDays() {
        return durationDays;
    }

    public UsageQuota getQuota() {
        return quota;
    }

    public Boolean isFullCurriculumAccess() {
        return fullCurriculumAccess;
    }

    public Boolean isProgressAnalyticsEnabled() {
        return progressAnalyticsEnabled;
    }

    public Boolean isSampleAnswerEnabled() {
        return sampleAnswerEnabled;
    }

    public Double getMaxAnswerTimeSeconds() {
        return maxAnswerTimeSeconds;
    }

    public Boolean isSaveAnswerHistoryEnabled() {
        return saveAnswerHistoryEnabled;
    }

    public PlanStatus getStatus() {
        return status;
    }

    public static final class Builder {
        private Long id;
        private PlanCode code;
        private String name;
        private String description;
        private PlanTier tier;
        private Money price;
        private Integer durationDays;
        private UsageQuota quota;
        private Boolean fullCurriculumAccess;
        private Boolean progressAnalyticsEnabled;
        private Boolean sampleAnswerEnabled;
        private Double maxAnswerTimeSeconds;
        private Boolean saveAnswerHistoryEnabled;
        private PlanStatus status;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder code(PlanCode code) {
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

        public Builder durationDays(Integer durationDays) {
            this.durationDays = durationDays;
            return this;
        }

        public Builder quota(UsageQuota quota) {
            this.quota = quota;
            return this;
        }

        public Builder fullCurriculumAccess(Boolean fullCurriculumAccess) {
            this.fullCurriculumAccess = fullCurriculumAccess;
            return this;
        }

        public Builder progressAnalyticsEnabled(Boolean progressAnalyticsEnabled) {
            this.progressAnalyticsEnabled = progressAnalyticsEnabled;
            return this;
        }

        public Builder sampleAnswerEnabled(Boolean sampleAnswerEnabled) {
            this.sampleAnswerEnabled = sampleAnswerEnabled;
            return this;
        }

        public Builder maxAnswerTimeSeconds(Double maxAnswerTimeSeconds) {
            this.maxAnswerTimeSeconds = maxAnswerTimeSeconds;
            return this;
        }

        public Builder saveAnswerHistoryEnabled(Boolean saveAnswerHistoryEnabled) {
            this.saveAnswerHistoryEnabled = saveAnswerHistoryEnabled;
            return this;
        }

        public Builder status(PlanStatus status) {
            this.status = status;
            return this;
        }

        public SubscriptionPlan build() {
            if (code == null) {
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
            if (durationDays == null || durationDays <= 0) {
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
            return new SubscriptionPlan(this);
        }
    }
}
