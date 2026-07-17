package org.naho.learning.model;

import java.time.Instant;

public class UserLearningProgress {
    private Long id;
    private final Long farthestAvailableNodeId;
    private Long lastLearningNodeId;
    private Instant lastLearningAt;
    private final Integer currentStreak;
    private final Integer longestStreak;
    private Double totalPoint;

    private UserLearningProgress(Builder builder) {
        this.id = builder.id;
        this.farthestAvailableNodeId = builder.farthestAvailableNodeId;
        this.lastLearningNodeId = builder.lastLearningNodeId;
        this.lastLearningAt = builder.lastLearningAt;
        this.currentStreak = builder.currentStreak;
        this.longestStreak = builder.longestStreak;
        this.totalPoint = builder.totalPoint;
    }

    private UserLearningProgress(Long farthestAvailableNodeId, Integer currentStreak, Integer longestStreak, Double totalPoint) {
        this.farthestAvailableNodeId = farthestAvailableNodeId;
        this.currentStreak = currentStreak;
        this.longestStreak = longestStreak;
        this.totalPoint = totalPoint;
    }

    public static UserLearningProgress init(Long farthestAvailableNodeId) {
        return new UserLearningProgress(farthestAvailableNodeId, 0, 0, 0.0);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getFarthestAvailableNodeId() {
        return farthestAvailableNodeId;
    }

    public Long getLastLearningNodeId() {
        return lastLearningNodeId;
    }

    public Instant getLastLearningAt() {
        return lastLearningAt;
    }

    public Integer getCurrentStreak() {
        return currentStreak;
    }

    public Integer getLongestStreak() {
        return longestStreak;
    }

    public Double getTotalPoint() {
        return totalPoint;
    }

    public void setTotalPoint(Double totalPoint) {
        this.totalPoint = totalPoint;
    }

    public void addPoint(Double point) {
        if (this.totalPoint == null) {
            this.totalPoint = 0.0;
        }
        this.totalPoint += point;
    }

    public static final class Builder {
        private Long id;
        private Long farthestAvailableNodeId;
        private Long lastLearningNodeId;
        private Instant lastLearningAt;
        private Integer currentStreak;
        private Integer longestStreak;
        private Double totalPoint;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder farthestAvailableNodeId(Long farthestAvailableNodeId) {
            this.farthestAvailableNodeId = farthestAvailableNodeId;
            return this;
        }

        public Builder lastLearningNodeId(Long lastLearningNodeId) {
            this.lastLearningNodeId = lastLearningNodeId;
            return this;
        }

        public Builder lastLearningAt(Instant lastLearningAt) {
            this.lastLearningAt = lastLearningAt;
            return this;
        }

        public Builder currentStreak(Integer currentStreak) {
            this.currentStreak = currentStreak;
            return this;
        }

        public Builder longestStreak(Integer longestStreak) {
            this.longestStreak = longestStreak;
            return this;
        }

        public Builder totalPoint(Double totalPoint) {
            this.totalPoint = totalPoint;
            return this;
        }

        public UserLearningProgress build() {
            return new UserLearningProgress(this);
        }
    }
}
