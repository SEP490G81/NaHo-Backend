package org.naho.speech.model;

import java.time.Instant;

public class AnswerHistory {
    private Long id;
    private Long userId;
    private Long speakingQuestionId;
    private Long audioFileId;
    private Integer durationSec;
    private Instant createdTime;

    private AnswerHistory(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.speakingQuestionId = builder.speakingQuestionId;
        this.audioFileId = builder.audioFileId;
        this.durationSec = builder.durationSec;
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

    public Long getSpeakingQuestionId() {
        return speakingQuestionId;
    }

    public Long getAudioFileId() {
        return audioFileId;
    }

    public void setAudioFileId(Long audioFileId) {
        this.audioFileId = audioFileId;
    }

    public Integer getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(Integer durationSec) {
        this.durationSec = durationSec;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private Long speakingQuestionId;
        private Long audioFileId;
        private Integer durationSec;
        private Instant createdTime;

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

        public Builder audioFileId(Long audioFileId) {
            this.audioFileId = audioFileId;
            return this;
        }

        public Builder durationSec(Integer durationSec) {
            this.durationSec = durationSec;
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