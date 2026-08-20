package org.naho.speech.llm.conversation.result;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;
import java.util.List;

public record SpeakingSessionResult(
        Long id,
        String sessionCode,
        Long userId,
        Long personaId,

        String topic,
        String voiceName,
        MarugotoLevel marugotoLevel,
        FormalityLevel formalityLevel,
        Integer durationSeconds,
        int totalTurns,
        Double asrConfidence,
        String fullTranscript,
        SpeakingSessionStatus status,
        Instant startedAt,
        Instant endedAt,

        SpeakingSessionAssessmentResult speakingSessionAssessment,
        List<SpeakingSessionMessageResult> speakingSessionMessages
) {

    public static Builder builder() {
        return new Builder();
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
        private SpeakingSessionAssessmentResult speakingSessionAssessment;
        private List<SpeakingSessionMessageResult> speakingSessionMessages;

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

        public Builder speakingSessionAssessment(SpeakingSessionAssessmentResult speakingSessionAssessment) {
            this.speakingSessionAssessment = speakingSessionAssessment;
            return this;
        }

        public Builder speakingSessionMessages(List<SpeakingSessionMessageResult> speakingSessionMessages) {
            this.speakingSessionMessages = speakingSessionMessages;
            return this;
        }

        public SpeakingSessionResult build() {
            return new SpeakingSessionResult(
                    id,
                    sessionCode,
                    userId,
                    personaId,
                    topic,
                    voiceName,
                    marugotoLevel,
                    formalityLevel,
                    durationSeconds,
                    totalTurns,
                    asrConfidence,
                    fullTranscript,
                    status,
                    startedAt,
                    endedAt,
                    speakingSessionAssessment,
                    speakingSessionMessages
            );
        }
    }
}
