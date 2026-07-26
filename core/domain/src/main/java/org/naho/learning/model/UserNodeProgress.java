package org.naho.learning.model;

import org.naho.i18n.message.learning.UserNodeProgressDetailMessageKey;
import org.naho.learning.exception.UserNodeProgressDomainErrorCode;
import org.naho.learning.type.NodeStatus;
import org.naho.shared.exception.DomainException;

import java.time.Instant;

public class UserNodeProgress {
    private final Long id;
    private final Long learningPathNodeId;
    private final Long userId;

    private Double bestScore;
    private Double currentScore;
    private Integer attemptCount;
    private Instant completedAt;
    private NodeStatus status;

    private UserNodeProgress(Builder builder) {
        this.id = builder.id;
        this.learningPathNodeId = builder.learningPathNodeId;
        this.userId = builder.userId;
        this.bestScore = builder.bestScore;
        this.currentScore = builder.currentScore;
        this.attemptCount = builder.attemptCount;
        this.completedAt = builder.completedAt;
        this.status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void increaseAttemptCount() {
        this.attemptCount = this.attemptCount == null ? 1 : this.attemptCount + 1;
    }

    public void updateBestScore(Double bestScore) {
        if (this.bestScore == null || this.bestScore < bestScore) {
            this.bestScore = bestScore;
        }
    }

    public Long getId() {
        return id;
    }

    public Long getLearningPathNodeId() {
        return learningPathNodeId;
    }

    public Long getUserId() {
        return userId;
    }

    public Double getBestScore() {
        return bestScore;
    }

    public void setBestScore(Double bestScore) {
        this.bestScore = bestScore;
    }

    public Double getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Double currentScore) {
        this.currentScore = currentScore;
    }

    public Integer getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(Integer attemptCount) {
        this.attemptCount = attemptCount;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public static final class Builder {

        private Long id;
        private Long learningPathNodeId;
        private Long userId;
        private Double bestScore;
        private Double currentScore;
        private Integer attemptCount;
        private Instant completedAt;
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

        public Builder completedAt(Instant completedAt) {
            this.completedAt = completedAt;
            return this;
        }

        public Builder status(NodeStatus status) {
            this.status = status;
            return this;
        }

        public UserNodeProgress build() {
            if (learningPathNodeId == null) {
                throw new DomainException(
                        UserNodeProgressDomainErrorCode.USER_NODE_PROGRESS_NODE_EMPTY,
                        UserNodeProgressDetailMessageKey.USER_NODE_PROGRESS_NODE_EMPTY
                );
            }

            if (userId == null) {
                throw new DomainException(
                        UserNodeProgressDomainErrorCode.USER_NODE_PROGRESS_USER_EMPTY,
                        UserNodeProgressDetailMessageKey.USER_NODE_PROGRESS_USER_EMPTY
                );
            }
            return new UserNodeProgress(this);
        }
    }
}
