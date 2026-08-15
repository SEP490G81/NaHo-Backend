package org.naho.speech.azure.model;

import org.naho.speech.azure.type.SpeechAssessmentErrorType;

public class WordAssessment {
    private final Long id;
    private final Long speechAssessmentId;
    private final String word;
    private final Double accuracyScore;
    private final SpeechAssessmentErrorType errorType; // Omission, Insertion, Mispronunciation, None

    private final String wordMarkup;

    private WordAssessment(Builder builder) {
        this.id = builder.id;
        this.word = builder.word;
        this.accuracyScore = builder.accuracyScore;
        this.errorType = builder.errorType;
        this.speechAssessmentId = builder.speechAssessmentId;
        this.wordMarkup = builder.wordMarkup;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    // Getter

    public String getWord() {
        return word;
    }

    public Double getAccuracyScore() {
        return accuracyScore;
    }

    public SpeechAssessmentErrorType getErrorType() {
        return errorType;
    }

    public Long getSpeechAssessmentId() {
        return speechAssessmentId;
    }

    public String getWordMarkup() {
        return wordMarkup;
    }

    public static class Builder {
        private Long id;
        private String word;
        private Double accuracyScore;
        private SpeechAssessmentErrorType errorType;
        private Long speechAssessmentId;
        private String wordMarkup;

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

        public Builder speechAssessmentId(Long speechAssessmentId) {
            this.speechAssessmentId = speechAssessmentId;
            return this;
        }

        public Builder wordMarkup(String wordMarkup) {
            this.wordMarkup = wordMarkup;
            return this;
        }

        public WordAssessment build() {
            return new WordAssessment(this);
        }
    }
}