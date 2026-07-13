package org.naho.learning.model;

import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.learning.exception.UserLearningProgressDomainErrorCode;
import org.naho.shared.exception.DomainException;

public class UserLearningProgress {
    private final Long id;
    private final Long learningPathNodeId;
    private final Long userId;

    private UserLearningProgress(Builder builder) {
        this.id = builder.id;
        this.learningPathNodeId = builder.learningPathNodeId;
        this.userId = builder.userId;
    }

    public static Builder builder() {
        return new Builder();
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

    public static final class Builder {

        private Long id;
        private Long learningPathNodeId;
        private Long userId;

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

        public UserLearningProgress build() {
            if (learningPathNodeId == null) {
                throw new DomainException(
                        UserLearningProgressDomainErrorCode.USER_LEARNING_PROGRESS_LEARNING_PATH_NODE_EMPTY,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_LEARNING_PATH_NODE_EMPTY
                );
            }

            if (userId == null) {
                throw new DomainException(
                        UserLearningProgressDomainErrorCode.USER_LEARNING_PROGRESS_USER_EMPTY,
                        UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_USER_EMPTY
                );
            }
            return new UserLearningProgress(this);
        }
    }
}
