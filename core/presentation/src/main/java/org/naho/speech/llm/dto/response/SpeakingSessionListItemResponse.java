package org.naho.speech.llm.dto.response;

import java.time.Instant;

public record SpeakingSessionListItemResponse(
        Long id,
        String sessionCode,
        String sessionType,
        String topic,
        Long personaId,
        String marugotoLevel,
        String formalityLevel,
        int overallScore,
        String jlptEstimate,
        int totalTurns,
        int durationSeconds,
        Instant startedAt,
        Instant endedAt
) {
}
