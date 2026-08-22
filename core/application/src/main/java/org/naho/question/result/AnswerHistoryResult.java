package org.naho.question.result;

import org.naho.file.result.FileResult;
import org.naho.speech.azure.result.SpeechAssessmentResult;
import org.naho.speech.llm.question.result.AiFeedbackResult;

import java.time.Instant;

public class AnswerHistoryResult {

    private Long id;
    private Long userId;
    private SpeakingQuestionResult speakingQuestion;
    private SpeechAssessmentResult speechAssessment;
    private AiFeedbackResult aiFeedback;
    private FileResult audioFile;
    private Double duration;
    private Double overallScore;
    private Instant createdTime;
    private Instant modifiedTime;

    private AnswerHistoryResult(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.speakingQuestion = builder.speakingQuestion;
        this.speechAssessment = builder.speechAssessment;
        this.aiFeedback = builder.aiFeedback;
        this.audioFile = builder.audioFile;
        this.duration = builder.duration;
        this.overallScore = builder.overallScore;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
    }

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

    public SpeakingQuestionResult getSpeakingQuestion() {
        return speakingQuestion;
    }

    public void setSpeakingQuestion(SpeakingQuestionResult speakingQuestion) {
        this.speakingQuestion = speakingQuestion;
    }

    public SpeechAssessmentResult getSpeechAssessment() {
        return speechAssessment;
    }

    public void setSpeechAssessment(SpeechAssessmentResult speechAssessment) {
        this.speechAssessment = speechAssessment;
    }

    public AiFeedbackResult getAiFeedback() {
        return aiFeedback;
    }

    public void setAiFeedback(AiFeedbackResult aiFeedback) {
        this.aiFeedback = aiFeedback;
    }

    public FileResult getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(FileResult audioFile) {
        this.audioFile = audioFile;
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

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public Instant getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Instant modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private SpeakingQuestionResult speakingQuestion;
        private SpeechAssessmentResult speechAssessment;
        private AiFeedbackResult aiFeedback;
        private FileResult audioFile;
        private Double duration;
        private Double overallScore;
        private Instant createdTime;
        private Instant modifiedTime;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder speakingQuestion(
                SpeakingQuestionResult speakingQuestion
        ) {
            this.speakingQuestion = speakingQuestion;
            return this;
        }

        public Builder speechAssessment(
                SpeechAssessmentResult speechAssessment
        ) {
            this.speechAssessment = speechAssessment;
            return this;
        }

        public Builder aiFeedback(
                AiFeedbackResult aiFeedback
        ) {
            this.aiFeedback = aiFeedback;
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

        public Builder createdTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public Builder modifiedTime(Instant modifiedTime) {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public AnswerHistoryResult build() {
            return new AnswerHistoryResult(this);
        }
    }
}