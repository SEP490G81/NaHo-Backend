package org.naho.speech.llm.model.conversation;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;
import java.util.List;

public class SpeakingSession {

    private Long id;
    private String sessionCode;
    private Long userId;
    private Long personaId;

    private String topic;
    private String voiceName;
    private MarugotoLevel marugotoLevel;
    private FormalityLevel formalityLevel;
    private Integer totalTurns;
    private SpeakingSessionStatus status;
    private Instant startedAt;
    private Instant endedAt;

    private SpeakingSessionAssessment speakingSessionAssessment;
    private List<SpeakingSessionMessage> speakingSessionMessages;

    private SpeakingSession(Builder builder) {
        this.id = builder.id;
        this.sessionCode = builder.sessionCode;
        this.userId = builder.userId;
        this.personaId = builder.personaId;

        this.topic = builder.topic;
        this.voiceName = builder.voiceName;
        this.marugotoLevel = builder.marugotoLevel;
        this.formalityLevel = builder.formalityLevel;
        this.totalTurns = builder.totalTurns;
        this.status = builder.status;
        this.startedAt = builder.startedAt;
        this.endedAt = builder.endedAt;
        this.speakingSessionAssessment = builder.speakingSessionAssessment;
        this.speakingSessionMessages = builder.speakingSessionMessages;
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

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPersonaId() {
        return personaId;
    }

    public void setPersonaId(Long personaId) {
        this.personaId = personaId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getVoiceName() {
        return voiceName;
    }

    public void setVoiceName(String voiceName) {
        this.voiceName = voiceName;
    }

    public MarugotoLevel getMarugotoLevel() {
        return marugotoLevel;
    }

    public void setMarugotoLevel(MarugotoLevel marugotoLevel) {
        this.marugotoLevel = marugotoLevel;
    }

    public FormalityLevel getFormalityLevel() {
        return formalityLevel;
    }

    public void setFormalityLevel(FormalityLevel formalityLevel) {
        this.formalityLevel = formalityLevel;
    }

    public Integer getTotalTurns() {
        return totalTurns;
    }

    public void setTotalTurns(Integer totalTurns) {
        this.totalTurns = totalTurns;
    }

    public SpeakingSessionStatus getStatus() {
        return status;
    }

    public void setStatus(SpeakingSessionStatus status) {
        this.status = status;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Instant endedAt) {
        this.endedAt = endedAt;
    }

    public SpeakingSessionAssessment getSpeakingSessionAssessment() {
        return speakingSessionAssessment;
    }

    public void setSpeakingSessionAssessment(SpeakingSessionAssessment speakingSessionAssessment) {
        this.speakingSessionAssessment = speakingSessionAssessment;
    }

    public List<SpeakingSessionMessage> getSpeakingSessionMessages() {
        return speakingSessionMessages;
    }

    public void setSpeakingSessionMessages(List<SpeakingSessionMessage> speakingSessionMessages) {
        this.speakingSessionMessages = speakingSessionMessages;
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
        private Integer totalTurns;
        private SpeakingSessionStatus status;
        private Instant startedAt;
        private Instant endedAt;
        private SpeakingSessionAssessment speakingSessionAssessment;
        private List<SpeakingSessionMessage> speakingSessionMessages;

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

        public Builder totalTurns(Integer totalTurns) {
            this.totalTurns = totalTurns;
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

        public Builder speakingSessionAssessment(SpeakingSessionAssessment speakingSessionAssessment) {
            this.speakingSessionAssessment = speakingSessionAssessment;
            return this;
        }

        public Builder speakingSessionMessages(List<SpeakingSessionMessage> speakingSessionMessages) {
            this.speakingSessionMessages = speakingSessionMessages;
            return this;
        }

        public SpeakingSession build() {
            return new SpeakingSession(this);
        }
    }
}