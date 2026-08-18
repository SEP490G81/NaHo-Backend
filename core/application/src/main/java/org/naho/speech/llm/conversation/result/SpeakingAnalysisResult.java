package org.naho.speech.llm.conversation.result;

import org.naho.file.result.FileResult;

public class SpeakingAnalysisResult {
    private Long answerHistoryId;
    private Double overallScore;
    private FileResult audioFile;
    private SpeakingAnalysisReportResult report;


    private SpeakingAnalysisResult(Long answerHistoryId, Double overallScore, FileResult audioFile, SpeakingAnalysisReportResult report) {
        this.answerHistoryId = answerHistoryId;
        this.overallScore = overallScore;
        this.audioFile = audioFile;
        this.report = report;
    }

    public static Builder builder() {
        return new Builder();
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

    public SpeakingAnalysisReportResult getReport() {
        return report;
    }

    public void setReport(SpeakingAnalysisReportResult report) {
        this.report = report;
    }

    public static final class Builder {
        private Long answerHistoryId;
        private Double overallScore;
        private FileResult audioFile;
        private SpeakingAnalysisReportResult report;

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

        public Builder report(SpeakingAnalysisReportResult report) {
            this.report = report;
            return this;
        }

        public SpeakingAnalysisResult build() {
            return new SpeakingAnalysisResult(
                    answerHistoryId,
                    overallScore,
                    audioFile,
                    report
            );
        }
    }
}

