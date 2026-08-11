package org.naho.learning.result;

import org.naho.learning.type.NodeStatus;

import java.time.Instant;

public record UserNodeProgressResult(
        Long id,
        Long learningPathNodeId,
        Long userId,
        Double bestScore,
        Double currentScore,
        Integer attemptCount,
        Instant lastCompletedAt,
        NodeStatus status
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private Long learningPathNodeId;
        private Long userId;
        private Double bestScore;
        private Double currentScore;
        private Integer attemptCount;
        private Instant lastCompletedAt;
        private NodeStatus status;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder learningPathNodeId(Long learningPathNodeId) {
            this.learningPathNodeId = learningPathNodeId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder bestScore(Double bestScore) {
            this.bestScore = bestScore;
            return this;
        }

        public Builder currentScore(Double currentScore) {
            this.currentScore = currentScore;
            return this;
        }

        public Builder attemptCount(Integer attemptCount) {
            this.attemptCount = attemptCount;
            return this;
        }

        public Builder lastCompletedAt(Instant lastCompletedAt) {
            this.lastCompletedAt = lastCompletedAt;
            return this;
        }

        public Builder status(NodeStatus status) {
            this.status = status;
            return this;
        }

        public UserNodeProgressResult build() {
            return new UserNodeProgressResult(
                    id,
                    learningPathNodeId,
                    userId,
                    bestScore,
                    currentScore,
                    attemptCount,
                    lastCompletedAt,
                    status
            );
        }
    }
}