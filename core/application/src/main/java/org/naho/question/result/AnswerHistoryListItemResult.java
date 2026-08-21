package org.naho.question.result;

import org.naho.file.result.FileResult;

public class AnswerHistoryListItemResult {

    private Long id;
    private Long userId;
    private SpeakingQuestionListItemResult speakingQuestion;
    private FileResult audioFile;
    private Double duration;
    private Double overallScore;

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

    public SpeakingQuestionListItemResult getSpeakingQuestion() {
        return speakingQuestion;
    }

    public void setSpeakingQuestion(SpeakingQuestionListItemResult speakingQuestion) {
        this.speakingQuestion = speakingQuestion;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private SpeakingQuestionListItemResult speakingQuestion;
        private FileResult audioFile;
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

        public Builder speakingQuestion(
                SpeakingQuestionListItemResult speakingQuestion
        ) {
            this.speakingQuestion = speakingQuestion;
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

        public AnswerHistoryListItemResult build() {
            AnswerHistoryListItemResult result = new AnswerHistoryListItemResult();

            result.id = id;
            result.userId = userId;
            result.speakingQuestion = speakingQuestion;
            result.audioFile = audioFile;
            result.duration = duration;
            result.overallScore = overallScore;

            return result;
        }
    }
}
