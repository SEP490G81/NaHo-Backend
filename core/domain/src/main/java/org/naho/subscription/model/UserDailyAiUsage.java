package org.naho.subscription.model;

import java.time.LocalDate;

public class UserDailyAiUsage {

    private Long id;
    private Long userId;
    private LocalDate usageDate;

    // Số lượt được AI chấm điểm trong Speaking Question
    private Integer speakingEvaluationCount;

    // Số lượt được AI chấm điểm trong AI 1:1
    private Integer aiSessionEvaluationCount;

    private UserDailyAiUsage(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.usageDate = builder.usageDate;
        this.speakingEvaluationCount = builder.speakingEvaluationCount;
        this.aiSessionEvaluationCount = builder.aiSessionEvaluationCount;
    }

    public static UserDailyAiUsage init(Long userId, LocalDate usageDate) {
        return UserDailyAiUsage.builder()
                .userId(userId)
                .usageDate(usageDate)
                .speakingEvaluationCount(0)
                .aiSessionEvaluationCount(0)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getUsageDate() {
        return usageDate;
    }

    public Integer getSpeakingEvaluationCount() {
        return speakingEvaluationCount;
    }

    public Integer getAiSessionEvaluationCount() {
        return aiSessionEvaluationCount;
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private LocalDate usageDate;
        private Integer speakingEvaluationCount;
        private Integer aiSessionEvaluationCount;

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

        public Builder aiSessionEvaluationCount(Integer aiSessionEvaluationCount) {
            this.aiSessionEvaluationCount = aiSessionEvaluationCount;
            return this;
        }

        public UserDailyAiUsage build() {
            return new UserDailyAiUsage(this);
        }
    }
}
