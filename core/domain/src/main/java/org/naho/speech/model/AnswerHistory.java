package org.naho.speech.model;

import java.time.Instant;

public class AnswerHistory {
    private Long id;
    private Long userId;
    private Long questionId;
    private Long audioFileId;
    private Instant createdTime;

    private AnswerHistory(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.questionId = builder.questionId;
        this.audioFileId = builder.audioFileId;
        this.createdTime = builder.createdTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getAudioFileId() {
        return audioFileId;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long questionId;
        private Long audioFileId;
        private Instant createdTime;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder questionId(Long questionId) {
            this.questionId = questionId;
            return this;
        }

        public Builder audioFileId(Long audioFileId) {
            this.audioFileId = audioFileId;
            return this;
        }

        public Builder createdTime(Instant createdTime) {
            this.createdTime = createdTime;
            return this;
        }

        public AnswerHistory build() {
            return new AnswerHistory(this);
        }
    }
}