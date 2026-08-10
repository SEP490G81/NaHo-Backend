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
    private String description;
    private PlanTier tier;
    private Money price;
    private Integer durationDays;
    private Integer dailySpeakingQuestionEvaluationLimit;
    private Integer maxSpeakingQuestionRecordingSeconds;
    private Integer maxConcurrentAiSessionCount;
    private Integer maxTurnsPerAiSession;
    private Integer dailyAiSessionEvaluationLimit;
    private Integer maxAiTurnSpeakingSeconds;
    private Boolean sampleAnswerEnabled;
    private PlanStatus status;

    private SubscriptionPlan(Builder builder) {
        this.id = builder.id;
        this.code = builder.code;
        this.description = builder.description;
        this.tier = builder.tier;
        this.price = builder.price;
        this.durationDays = builder.durationDays;
        this.dailySpeakingQuestionEvaluationLimit = builder.dailySpeakingQuestionEvaluationLimit;
        this.maxSpeakingQuestionRecordingSeconds = builder.maxSpeakingQuestionRecordingSeconds;
        this.maxConcurrentAiSessionCount = builder.maxConcurrentAiSessionCount;
        this.maxTurnsPerAiSession = builder.maxTurnsPerAiSession;
        this.dailyAiSessionEvaluationLimit = builder.dailyAiSessionEvaluationLimit;
        this.maxAiTurnSpeakingSeconds = builder.maxAiTurnSpeakingSeconds;
        this.sampleAnswerEnabled = builder.sampleAnswerEnabled;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static SubscriptionPlan create(
            PlanCode code,
            String description,
            PlanTier tier,
            Money price,
            Integer durationDays,
            Integer dailySpeakingQuestionEvaluationLimit,
            Integer maxSpeakingQuestionRecordingSeconds,
            Integer maxConcurrentAiSessionCount,
            Integer maxTurnsPerAiSession,
            Integer dailyAiSessionEvaluationLimit,
            Integer maxAiTurnSpeakingSeconds,
            Boolean sampleAnswerEnabled) {
        return builder()
                .code(code)
                .description(description)
                .tier(tier)
                .price(price)
                .durationDays(durationDays)
                .dailySpeakingQuestionEvaluationLimit(dailySpeakingQuestionEvaluationLimit)
                .maxSpeakingQuestionRecordingSeconds(maxSpeakingQuestionRecordingSeconds)
                .maxConcurrentAiSessionCount(maxConcurrentAiSessionCount)
                .maxTurnsPerAiSession(maxTurnsPerAiSession)
                .dailyAiSessionEvaluationLimit(dailyAiSessionEvaluationLimit)
                .maxAiTurnSpeakingSeconds(maxAiTurnSpeakingSeconds)
                .sampleAnswerEnabled(sampleAnswerEnabled)
                .status(PlanStatus.ACTIVE)
                .build();
    }

    public boolean isAvailableForPurchase() {
        return status == PlanStatus.ACTIVE;
    }

    public boolean isFree() {
        return price != null && price.isZero();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public PlanCode getCode() {
        return code;
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

    public Integer getDailySpeakingQuestionEvaluationLimit() {
        return dailySpeakingQuestionEvaluationLimit;
    }

    public Integer getMaxSpeakingQuestionRecordingSeconds() {
        return maxSpeakingQuestionRecordingSeconds;
    }

    public Integer getMaxConcurrentAiSessionCount() {
        return maxConcurrentAiSessionCount;
    }

    public Integer getMaxTurnsPerAiSession() {
        return maxTurnsPerAiSession;
    }

    public Integer getDailyAiSessionEvaluationLimit() {
        return dailyAiSessionEvaluationLimit;
    }

    public Integer getMaxAiTurnSpeakingSeconds() {
        return maxAiTurnSpeakingSeconds;
    }

    public Boolean isSampleAnswerEnabled() {
        return sampleAnswerEnabled;
    }

    public PlanStatus getStatus() {
        return status;
    }

    public static final class Builder {
        private Long id;
        private PlanCode code;
        private String description;
        private PlanTier tier;
        private Money price;
        private Integer durationDays;
        private Integer dailySpeakingQuestionEvaluationLimit;
        private Integer maxSpeakingQuestionRecordingSeconds;
        private Integer maxConcurrentAiSessionCount;
        private Integer maxTurnsPerAiSession;
        private Integer dailyAiSessionEvaluationLimit;
        private Integer maxAiTurnSpeakingSeconds;
        private Boolean sampleAnswerEnabled;
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

        public Builder dailySpeakingQuestionEvaluationLimit(Integer dailySpeakingQuestionEvaluationLimit) {
            this.dailySpeakingQuestionEvaluationLimit = dailySpeakingQuestionEvaluationLimit;
            return this;
        }

        public Builder maxSpeakingQuestionRecordingSeconds(Integer maxSpeakingQuestionRecordingSeconds) {
            this.maxSpeakingQuestionRecordingSeconds = maxSpeakingQuestionRecordingSeconds;
            return this;
        }

        public Builder maxConcurrentAiSessionCount(Integer maxConcurrentAiSessionCount) {
            this.maxConcurrentAiSessionCount = maxConcurrentAiSessionCount;
            return this;
        }

        public Builder maxTurnsPerAiSession(Integer maxTurnsPerAiSession) {
            this.maxTurnsPerAiSession = maxTurnsPerAiSession;
            return this;
        }

        public Builder dailyAiSessionEvaluationLimit(Integer dailyAiSessionEvaluationLimit) {
            this.dailyAiSessionEvaluationLimit = dailyAiSessionEvaluationLimit;
            return this;
        }

        public Builder maxAiTurnSpeakingSeconds(Integer maxAiTurnSpeakingSeconds) {
            this.maxAiTurnSpeakingSeconds = maxAiTurnSpeakingSeconds;
            return this;
        }

        public Builder sampleAnswerEnabled(Boolean sampleAnswerEnabled) {
            this.sampleAnswerEnabled = sampleAnswerEnabled;
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
            if (price == null) {
                throw new DomainException(SubscriptionDomainErrorCode.PLAN_PRICE_EMPTY,
                        SubscriptionDetailMessageKey.PLAN_PRICE_EMPTY);
            }
            if (durationDays != null && durationDays <= 0) {
                throw new DomainException(SubscriptionDomainErrorCode.PLAN_DURATION_INVALID,
                        SubscriptionDetailMessageKey.PLAN_DURATION_INVALID);
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
