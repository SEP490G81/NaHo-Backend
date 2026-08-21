package org.naho.question.result;

import org.naho.file.result.FileResult;

public class AnswerHistoryListItemResult {

    private Long id;
    private Long userId;
    private SpeakingQuestionListItemResult speakingQuestion;
    private FileResult audioFile;
    private Double duration;
    private Double overallScore;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private SpeakingQuestionListItemResult speakingQuestion;
        private FileResult audioFile;
        private Double duration;
        private Double overallScore;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder speakingQuestion(
                SpeakingQuestionListItemResult speakingQuestion
        ) {
            this.speakingQuestion = speakingQuestion;
            return this;
        }

        public Builder audioFile(FileResult audioFile) {
            this.audioFile = audioFile;
            return this;
        }

        public Builder duration(Double duration) {
            this.duration = duration;
            return this;
        }

        public Builder overallScore(Double overallScore) {
            this.overallScore = overallScore;
            return this;
        }

        public AnswerHistoryListItemResult build() {
            AnswerHistoryListItemResult result = new AnswerHistoryListItemResult();

            result.id = id;
            result.userId = userId;
            result.speakingQuestion = speakingQuestion;
            result.audioFile = audioFile;
            result.duration = duration;
            result.overallScore = overallScore;

            return result;
        }
    }
}
