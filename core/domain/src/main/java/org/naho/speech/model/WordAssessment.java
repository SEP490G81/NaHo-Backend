package org.naho.speech.model;

import org.naho.speech.type.SpeechAssessmentErrorType;

public class WordAssessment {
    private Long id;
    private Long speechAssessmentId;
    private String word;
    private Double accuracyScore;
    private SpeechAssessmentErrorType errorType; // Omission, Insertion, Mispronunciation, None

    private WordAssessment(Builder builder) {
        this.id = builder.id;
        this.word = builder.word;
        this.accuracyScore = builder.accuracyScore;
        this.errorType = builder.errorType;
        this.speechAssessmentId = builder.speechAssessmentId;
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

    public static class Builder {
        private Long id;
        private String word;
        private Double accuracyScore;
        private SpeechAssessmentErrorType errorType;
        private Long speechAssessmentId;

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

        public WordAssessment build() {
            return new WordAssessment(this);
        }
    }
}