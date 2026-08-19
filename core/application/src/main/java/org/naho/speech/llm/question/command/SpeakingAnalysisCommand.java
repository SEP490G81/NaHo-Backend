package org.naho.speech.llm.question.command;

import org.naho.file.result.StoredFile;

public record SpeakingAnalysisCommand(
        Long userId,
        Long speakingQuestionId,
        Double duration,
        StoredFile storedFile,
        byte[] audioBytes,
        Integer dailySpeakingQuestionEvaluationLimit
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long userId;
        private Long speakingQuestionId;
        private Double duration;
        private StoredFile storedFile;
        private byte[] audioBytes;
        private Integer dailySpeakingQuestionEvaluationLimit;

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder speakingQuestionId(Long speakingQuestionId) {
            this.speakingQuestionId = speakingQuestionId;
            return this;
        }

        public Builder duration(Double duration) {
            this.duration = duration;
            return this;
        }

        public Builder storedFile(StoredFile storedFile) {
            this.storedFile = storedFile;
            return this;
        }

        public Builder audioBytes(byte[] audioBytes) {
            this.audioBytes = audioBytes;
            return this;
        }

        public Builder dailySpeakingQuestionEvaluationLimit(
                Integer dailySpeakingQuestionEvaluationLimit
        ) {
            this.dailySpeakingQuestionEvaluationLimit =
                    dailySpeakingQuestionEvaluationLimit;
            return this;
        }

        public SpeakingAnalysisCommand build() {
            return new SpeakingAnalysisCommand(
                    userId,
                    speakingQuestionId,
                    duration,
                    storedFile,
                    audioBytes,
                    dailySpeakingQuestionEvaluationLimit
            );
        }
    }
}
