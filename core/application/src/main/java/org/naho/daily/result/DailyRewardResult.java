package org.naho.daily.result;

import org.naho.chest.result.ChestResult;

public record DailyRewardResult(
        Long id,
        ChestResult chest,
        String rewardYearMonth,
        Integer dayOfMonth
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long id;
        private ChestResult chest;
        private String rewardYearMonth;
        private Integer dayOfMonth;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder chest(ChestResult chest) {
            this.chest = chest;
            return this;
        }

        public Builder rewardYearMonth(String rewardYearMonth) {
            this.rewardYearMonth = rewardYearMonth;
            return this;
        }

        public Builder dayOfMonth(Integer dayOfMonth) {
            this.dayOfMonth = dayOfMonth;
            return this;
        }

        public DailyRewardResult build() {
            return new DailyRewardResult(
                    id,
                    chest,
                    rewardYearMonth,
                    dayOfMonth
            );
        }
    }
}