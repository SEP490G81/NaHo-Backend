package org.naho.speech.llm.model.conversation;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;

public class SpeakingSession {

    private Long id;
    private String sessionCode;
    private Long userId;
    private Long personaId;

    private String topic;
    private String voiceName;
    private MarugotoLevel marugotoLevel;
    private FormalityLevel formalityLevel;
    private Integer durationSeconds;
    private int totalTurns;
    private Double asrConfidence;
    private String fullTranscript;
    private SpeakingSessionStatus status;
    private Instant startedAt;
    private Instant endedAt;

    private SpeakingSession(Builder builder) {
        this.id = builder.id;
        this.sessionCode = builder.sessionCode;
        this.userId = builder.userId;
        this.personaId = builder.personaId;

        this.topic = builder.topic;
        this.voiceName = builder.voiceName;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public void setMarugotoLevel(MarugotoLevel marugotoLevel) {
        this.marugotoLevel = marugotoLevel;
    }

    public void setFormalityLevel(FormalityLevel formalityLevel) {
        this.formalityLevel = formalityLevel;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public void setTotalTurns(int totalTurns) {
        this.totalTurns = totalTurns;
    }

    public void setAsrConfidence(Double asrConfidence) {
        this.asrConfidence = asrConfidence;
    }

    public void setFullTranscript(String fullTranscript) {
        this.fullTranscript = fullTranscript;
    }

    public void setStatus(SpeakingSessionStatus status) {
        this.status = status;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long getId() {
        return id;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public String getTopic() {
        return topic;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public MarugotoLevel getMarugotoLevel() {
        return marugotoLevel;
    }

    public FormalityLevel getFormalityLevel() {
        return formalityLevel;
    }

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public int getTotalTurns() {
        return totalTurns;
    }

    public Double getAsrConfidence() {
        return asrConfidence;
    }

    public String getFullTranscript() {
        return fullTranscript;
    }

    public SpeakingSessionStatus getStatus() {
        return status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public static class Builder {

        private Long id;
        private String sessionCode;
        private Long userId;
        private Long personaId;

        private String topic;
        private String voiceName;
        private MarugotoLevel marugotoLevel;
        private FormalityLevel formalityLevel;
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

        public Builder sessionCode(String sessionCode) {
            this.sessionCode = sessionCode;
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

        public Builder voiceName(String voiceName) {
            this.voiceName = voiceName;
            return this;
        }

        public Builder marugotoLevel(MarugotoLevel marugotoLevel) {
            this.marugotoLevel = marugotoLevel;
            return this;
        }

        public Builder formalityLevel(FormalityLevel formalityLevel) {
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