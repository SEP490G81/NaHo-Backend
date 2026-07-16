package org.naho.learning.result;

import java.time.Instant;

public record UserLearningProgressResult(
        Long id,
        Long farthestAvailableNodeId,
        Long lastLearningNodeId,
        Instant lastLearningAt,
        Integer currentStreak,
        Integer longestStreak
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long farthestAvailableNodeId;
        private Long lastLearningNodeId;
        private Instant lastLearningAt;
        private Integer currentStreak;
        private Integer longestStreak;

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

        public UserLearningProgressResult build() {
            return new UserLearningProgressResult(
                    id,
                    farthestAvailableNodeId,
                    lastLearningNodeId,
                    lastLearningAt,
                    currentStreak,
                    longestStreak
            );
        }
    }
}
