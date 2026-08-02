package org.naho.speech.llm.result;

import org.naho.file.result.FileResult;

public class SpeakingAnalysisResult {
    private Long answerHistoryId;
    private Double overallScore;
    private FileResult audioFile;

    private SpeakingAnalysisResult(Long answerHistoryId, Double overallScore, FileResult audioFile) {
        this.answerHistoryId = answerHistoryId;
        this.overallScore = overallScore;
        this.audioFile = audioFile;
    }

    public Long getAnswerHistoryId() {
        return answerHistoryId;
    }

    public void setAnswerHistoryId(Long answerHistoryId) {
        this.answerHistoryId = answerHistoryId;
    }

    public Double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(Double overallScore) {
        this.overallScore = overallScore;
    }

    public FileResult getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(FileResult audioFile) {
        this.audioFile = audioFile;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private Long answerHistoryId;
        private Double overallScore;
        private FileResult audioFile;

        private Builder() {
        }

        public Builder answerHistoryId(Long answerHistoryId) {
            this.answerHistoryId = answerHistoryId;
            return this;
        }

        public Builder overallScore(Double overallScore) {
            this.overallScore = overallScore;
            return this;
        }

        public Builder audioFile(FileResult audioFile) {
            this.audioFile = audioFile;
            return this;
        }

        public SpeakingAnalysisResult build() {
            return new SpeakingAnalysisResult(
                    answerHistoryId,
                    overallScore,
                    audioFile
            );
        }
    }
}
