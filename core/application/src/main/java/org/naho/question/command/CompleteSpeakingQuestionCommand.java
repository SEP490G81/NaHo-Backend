package org.naho.question.command;

import org.naho.learning.model.LearningPathNode;
import org.naho.learning.model.UserLearningProgress;

public record CompleteSpeakingQuestionCommand(
        UserLearningProgress userLearningProgress,
        LearningPathNode speakingQuestionLearningPathNode,
        Long userId,
        Double overallScore
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UserLearningProgress userLearningProgress;
        private LearningPathNode speakingQuestionLearningPathNode;
        private Long userId;
        private Double overallScore;

        public Builder userLearningProgress(UserLearningProgress userLearningProgress) {
            this.userLearningProgress = userLearningProgress;
            return this;
        }

        public Builder speakingQuestionLearningPathNode(LearningPathNode speakingQuestionLearningPathNode) {
            this.speakingQuestionLearningPathNode = speakingQuestionLearningPathNode;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder overallScore(Double overallScore) {
            this.overallScore = overallScore;
            return this;
        }

        public CompleteSpeakingQuestionCommand build() {
            return new CompleteSpeakingQuestionCommand(
                    userLearningProgress,
                    speakingQuestionLearningPathNode,
                    userId,
                    overallScore
            );
        }
    }
}
