package org.naho.speech.azure.model;

public class AnswerHistory {

    private Long id;
    private Long userId;
    private Long speakingQuestionId;
    private Long speechAssessmentId;
    private Long aiFeedbackId;
    private Long audioFileId;
    private Double duration;
    private Double overallScore;

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSpeakingQuestionId() {
        return speakingQuestionId;
    }

    public void setSpeakingQuestionId(Long speakingQuestionId) {
        this.speakingQuestionId = speakingQuestionId;
    }

    public Long getSpeechAssessmentId() {
        return speechAssessmentId;
    }

    public void setSpeechAssessmentId(Long speechAssessmentId) {
        this.speechAssessmentId = speechAssessmentId;
    }

    public Long getAiFeedbackId() {
        return aiFeedbackId;
    }

    public void setAiFeedbackId(Long aiFeedbackId) {
        this.aiFeedbackId = aiFeedbackId;
    }

    public Long getAudioFileId() {
        return audioFileId;
    }

    public void setAudioFileId(Long audioFileId) {
        this.audioFileId = audioFileId;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private Long speakingQuestionId;
        private Long speechAssessmentId;
        private Long aiFeedbackId;
        private Long audioFileId;
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

        public Builder speakingQuestionId(Long speakingQuestionId) {
            this.speakingQuestionId = speakingQuestionId;
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

        public Builder audioFileId(Long audioFileId) {
            this.audioFileId = audioFileId;
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

        public AnswerHistory build() {
            AnswerHistory answerHistory = new AnswerHistory();

            answerHistory.id = this.id;
            answerHistory.userId = this.userId;
            answerHistory.speakingQuestionId = this.speakingQuestionId;
            answerHistory.speechAssessmentId = this.speechAssessmentId;
            answerHistory.aiFeedbackId = this.aiFeedbackId;
            answerHistory.audioFileId = this.audioFileId;
            answerHistory.duration = this.duration;
            answerHistory.overallScore = this.overallScore;

            return answerHistory;
        }
    }
}
