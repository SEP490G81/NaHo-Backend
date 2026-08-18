package org.naho.speech.llm.model.question;

public class SpeakingAnalysis {
    private Long id;
    private Long speechAssessmentId;
    private Long aiFeedbackId;
    private Long answerHistoryId;
    private Long audioFileId;
    private Double overallScore;

    private SpeakingAnalysis(Builder builder) {
        this.id = builder.id;
        this.speechAssessmentId = builder.speechAssessmentId;
        this.aiFeedbackId = builder.aiFeedbackId;
        this.answerHistoryId = builder.answerHistoryId;
        this.audioFileId = builder.audioFileId;
        this.overallScore = builder.overallScore;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private Long speechAssessmentId;
        private Long aiFeedbackId;
        private Long answerHistoryId;
        private Long audioFileId;
        private Double overallScore;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder speechAssessmentId(Long speechAssessmentId) {
            this.speechAssessmentId = speechAssessmentId;
            return this;
        }

        public Builder aiFeedbackId(Long aiFeedbackId) {
            this.aiFeedbackId = aiFeedbackId;
            return this;
        }

        public Builder answerHistoryId(Long answerHistoryId) {
            this.answerHistoryId = answerHistoryId;
            return this;
        }

        public Builder audioFileId(Long audioFileId) {
            this.audioFileId = audioFileId;
            return this;
        }

        public Builder overallScore(Double overallScore) {
            this.overallScore = overallScore;
            return this;
        }

        public SpeakingAnalysis build() {
            return new SpeakingAnalysis(this);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getSpeechAssessmentId() {
        return speechAssessmentId;
    }

    public Long getAiFeedbackId() {
        return aiFeedbackId;
    }

    public Long getAnswerHistoryId() {
        return answerHistoryId;
    }

    public Long getAudioFileId() {
        return audioFileId;
    }

    public Double getOverallScore() {
        return overallScore;
    }
}