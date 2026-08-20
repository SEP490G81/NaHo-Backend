package org.naho.speech.llm.conversation.dto.response;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;
import java.util.List;

public record SpeakingSessionResponse(
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

        SpeakingSessionAssessmentResponse speakingSessionAssessment,
        List<SpeakingSessionMessageResponse> speakingSessionMessages
) {
}
