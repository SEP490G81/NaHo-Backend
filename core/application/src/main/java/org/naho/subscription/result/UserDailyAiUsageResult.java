package org.naho.subscription.result;

import java.time.LocalDate;

public record UserDailyAiUsageResult(
        Long id,
        Long userId,
        LocalDate usageDate,
        Integer speakingEvaluationCount,
        Integer aiSessionStartCount
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private LocalDate usageDate;
        private Integer speakingEvaluationCount;
        private Integer aiSessionStartCount;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder usageDate(LocalDate usageDate) {
            this.usageDate = usageDate;
            return this;
        }

        public Builder speakingEvaluationCount(Integer speakingEvaluationCount) {
            this.speakingEvaluationCount = speakingEvaluationCount;
            return this;
        }

        public Builder aiSessionStartCount(Integer aiSessionStartCount) {
            this.aiSessionStartCount = aiSessionStartCount;
            return this;
        }

        public UserDailyAiUsageResult build() {
            return new UserDailyAiUsageResult(
                    id,
                    userId,
                    usageDate,
                    speakingEvaluationCount,
                    aiSessionStartCount
            );
        }
    }
}
