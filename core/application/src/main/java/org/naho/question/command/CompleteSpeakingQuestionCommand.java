package org.naho.question.command;

public record CompleteSpeakingQuestionCommand(
        Long speakingQuestionId,
        Long userId,
        Double overallScore
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long speakingQuestionId;
        private Long userId;
        private Double overallScore;

        public Builder speakingQuestionId(Long speakingQuestionId) {
            this.speakingQuestionId = speakingQuestionId;
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
                    speakingQuestionId,
                    userId,
                    overallScore
            );
        }
    }
}
