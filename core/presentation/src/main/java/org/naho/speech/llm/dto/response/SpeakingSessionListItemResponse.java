package org.naho.speech.llm.dto.response;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;

public record SpeakingSessionListItemResponse(
        Long id,
        String sessionCode,
        String topic,
        Long personaId,
        MarugotoLevel marugotoLevel,
        FormalityLevel formalityLevel,
        int overallScore,
        String jlptEstimate,
        int totalTurns,
        int durationSeconds,
        Instant startedAt,
        Instant endedAt,
        SpeakingSessionStatus status
) {
}
