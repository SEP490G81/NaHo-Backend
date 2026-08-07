package org.naho.speech.model;

public class AiPronunciationReference {
    private final Long id;
    private final Long overallFeedbackId;
    private final Double accuracyScore;
    private final Double fluencyScore;
    private final Double completenessScore;
    private final Double prosodyScore;

    private AiPronunciationReference(Builder builder) {
        this.id = builder.id;
        this.overallFeedbackId = builder.overallFeedbackId;
        this.accuracyScore = builder.accuracyScore;
        this.fluencyScore = builder.fluencyScore;
        this.completenessScore = builder.completenessScore;
        this.prosodyScore = builder.prosodyScore;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getOverallFeedbackId() {
        return overallFeedbackId;
    }

    public Double getAccuracyScore() {
        return accuracyScore;
    }

    public Double getFluencyScore() {
        return fluencyScore;
    }

    public Double getCompletenessScore() {
        return completenessScore;
    }

    public Double getProsodyScore() {
        return prosodyScore;
    }

    public static class Builder {
        private Long id;
        private Long overallFeedbackId;
        private Double accuracyScore;
        private Double fluencyScore;
        private Double completenessScore;
        private Double prosodyScore;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder overallFeedbackId(Long overallFeedbackId) {
            this.overallFeedbackId = overallFeedbackId;
            return this;
        }

        public Builder accuracyScore(Double accuracyScore) {
            this.accuracyScore = accuracyScore;
            return this;
        }

        public Builder fluencyScore(Double fluencyScore) {
            this.fluencyScore = fluencyScore;
            return this;
        }

        public Builder completenessScore(Double completenessScore) {
            this.completenessScore = completenessScore;
            return this;
        }

        public Builder prosodyScore(Double prosodyScore) {
            this.prosodyScore = prosodyScore;
            return this;
        }

        public AiPronunciationReference build() {
            return new AiPronunciationReference(this);
        }
    }
}
