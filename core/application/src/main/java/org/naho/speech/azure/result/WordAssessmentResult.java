package org.naho.speech.azure.result;

import org.naho.speech.azure.type.SpeechAssessmentErrorType;

public record WordAssessmentResult(
        Long id,
        String word,
        Double accuracyScore,
        SpeechAssessmentErrorType errorType
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private Long id;
        private String word;
        private Double accuracyScore;
        private SpeechAssessmentErrorType errorType;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder word(String word) {
            this.word = word;
            return this;
        }

        public Builder accuracyScore(Double accuracyScore) {
            this.accuracyScore = accuracyScore;
            return this;
        }

        public Builder errorType(SpeechAssessmentErrorType errorType) {
            this.errorType = errorType;
            return this;
        }

        public WordAssessmentResult build() {
            return new WordAssessmentResult(
                    id,
                    word,
                    accuracyScore,
                    errorType
            );
        }
    }
}