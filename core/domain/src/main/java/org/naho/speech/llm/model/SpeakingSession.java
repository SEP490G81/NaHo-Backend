package org.naho.speech.llm.model;

import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;

public class SpeakingSession {

    private Long id;
    private Long userId;
    private Long personaId;

    private String topic;
    private String marugotoLevel;
    private String formalityLevel;
    private Integer durationSeconds;
    private int totalTurns;
    private Double asrConfidence;
    private String fullTranscript;
    private SpeakingSessionStatus status;
    private Instant startedAt;
    private Instant endedAt;

    private SpeakingSession(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.personaId = builder.personaId;

        this.topic = builder.topic;
        this.marugotoLevel = builder.marugotoLevel;
        this.formalityLevel = builder.formalityLevel;
        this.durationSeconds = builder.durationSeconds;
        this.totalTurns = builder.totalTurns;
        this.asrConfidence = builder.asrConfidence;
        this.fullTranscript = builder.fullTranscript;
        this.status = builder.status;
        this.startedAt = builder.startedAt;
        this.endedAt = builder.endedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long id;
        private Long userId;
        private Long personaId;

        private String topic;
        private String marugotoLevel;
        private String formalityLevel;
        private Integer durationSeconds;
        private int totalTurns;
        private Double asrConfidence;
        private String fullTranscript;
        private SpeakingSessionStatus status;
        private Instant startedAt;
        private Instant endedAt;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder personaId(Long personaId) {
            this.personaId = personaId;
            return this;
        }

        public Builder topic(String topic) {
            this.topic = topic;
            return this;
        }

        public Builder marugotoLevel(String marugotoLevel) {
            this.marugotoLevel = marugotoLevel;
            return this;
        }

        public Builder formalityLevel(String formalityLevel) {
            this.formalityLevel = formalityLevel;
            return this;
        }

        public Builder durationSeconds(Integer durationSeconds) {
            this.durationSeconds = durationSeconds;
            return this;
        }

        public Builder totalTurns(int totalTurns) {
            this.totalTurns = totalTurns;
            return this;
        }

        public Builder asrConfidence(Double asrConfidence) {
            this.asrConfidence = asrConfidence;
            return this;
        }

        public Builder fullTranscript(String fullTranscript) {
            this.fullTranscript = fullTranscript;
            return this;
        }

        public Builder status(SpeakingSessionStatus status) {
            this.status = status;
            return this;
        }

        public Builder startedAt(Instant startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public Builder endedAt(Instant endedAt) {
            this.endedAt = endedAt;
            return this;
        }

        public SpeakingSession build() {
            return new SpeakingSession(this);
        }
    }
}
