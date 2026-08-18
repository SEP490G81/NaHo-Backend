package org.naho.speech.llm.question.result;

import org.naho.file.result.FileResult;
import org.naho.speech.azure.result.SpeechAssessmentResult;

public class SpeakingAnalysisResult {
    private Long id;
    private SpeechAssessmentResult speechAssessmentResult;
    private AiFeedbackResult aiFeedbackResult;
    private Long answerHistoryId;
    private FileResult audioFileResult;
    private Double overallScore;

    private SpeakingAnalysisResult(Builder builder) {
        this.id = builder.id;
        this.speechAssessmentResult = builder.speechAssessmentResult;
        this.aiFeedbackResult = builder.aiFeedbackResult;
        this.answerHistoryId = builder.answerHistoryId;
        this.audioFileResult = builder.audioFileResult;
        this.overallScore = builder.overallScore;
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

    public SpeechAssessmentResult getSpeechAssessmentResult() {
        return speechAssessmentResult;
    }

    public void setSpeechAssessmentResult(SpeechAssessmentResult speechAssessmentResult) {
        this.speechAssessmentResult = speechAssessmentResult;
    }

    public AiFeedbackResult getAiFeedbackResult() {
        return aiFeedbackResult;
    }

    public void setAiFeedbackResult(AiFeedbackResult aiFeedbackResult) {
        this.aiFeedbackResult = aiFeedbackResult;
    }

    public Long getAnswerHistoryId() {
        return answerHistoryId;
    }

    public void setAnswerHistoryId(Long answerHistoryId) {
        this.answerHistoryId = answerHistoryId;
    }

    public FileResult getAudioFileResult() {
        return audioFileResult;
    }

    public void setAudioFileResult(FileResult audioFileResult) {
        this.audioFileResult = audioFileResult;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public static final class Builder {
        private Long id;
        private SpeechAssessmentResult speechAssessmentResult;
        private AiFeedbackResult aiFeedbackResult;
        private Long answerHistoryId;
        private FileResult audioFileResult;
        private Double overallScore;

        private Builder() {
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder speechAssessmentResult(SpeechAssessmentResult speechAssessmentResult) {
            this.speechAssessmentResult = speechAssessmentResult;
            return this;
        }

        public Builder aiFeedbackResult(AiFeedbackResult aiFeedbackResult) {
            this.aiFeedbackResult = aiFeedbackResult;
            return this;
        }

        public Builder answerHistoryId(Long answerHistoryId) {
            this.answerHistoryId = answerHistoryId;
            return this;
        }

        public Builder audioFileResult(FileResult audioFileResult) {
            this.audioFileResult = audioFileResult;
            return this;
        }

        public Builder overallScore(Double overallScore) {
            this.overallScore = overallScore;
            return this;
        }

        public SpeakingAnalysisResult build() {
            return new SpeakingAnalysisResult(this);
        }
    }
}