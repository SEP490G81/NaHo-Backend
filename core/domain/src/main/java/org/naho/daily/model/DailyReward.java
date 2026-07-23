package org.naho.daily.model;

import org.naho.daily.exception.DailyRewardDomainErrorCode;
import org.naho.daily.valueobject.RewardYearMonth;
import org.naho.i18n.message.daily.DailyRewardDetailMessageKey;
import org.naho.shared.exception.DomainException;

public class DailyReward {

    private final Long id;
    private final RewardYearMonth rewardYearMonth;
    private final Integer dayOfMonth;
    private Long chestId;

    private DailyReward(Builder builder) {
        this.id = builder.id;
        this.chestId = builder.chestId;
        this.rewardYearMonth = builder.rewardYearMonth;
        this.dayOfMonth = builder.dayOfMonth;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getChestId() {
        return chestId;
    }

    public RewardYearMonth getRewardYearMonth() {
        return rewardYearMonth;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setChestId(Long chestId) {
        this.chestId = chestId;
    }

    public static final class Builder {
        private Long id;
        private Long chestId;
        private RewardYearMonth rewardYearMonth;
        private Integer dayOfMonth;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder chestId(Long chestId) {
            this.chestId = chestId;
            return this;
        }

        public Builder rewardYearMonth(RewardYearMonth rewardYearMonth) {
            this.rewardYearMonth = rewardYearMonth;
            return this;
        }

        public Builder dayOfMonth(Integer dayOfMonth) {
            this.dayOfMonth = dayOfMonth;
            return this;
        }

        public DailyReward build() {
            if (dayOfMonth == null) {
                throw new DomainException(
                        DailyRewardDomainErrorCode.DAILY_REWARD_DAY_OF_MONTH_REQUIRED,
                        DailyRewardDetailMessageKey.DAILY_REWARD_DAY_OF_MONTH_REQUIRED
                );
            }

            if (dayOfMonth < 1 || dayOfMonth > 31) {
                throw new DomainException(
                        DailyRewardDomainErrorCode.DAILY_REWARD_DAY_OF_MONTH_INVALID,
                        DailyRewardDetailMessageKey.DAILY_REWARD_DAY_OF_MONTH_INVALID
                );
            }

            return new DailyReward(this);
        }
    }
}
