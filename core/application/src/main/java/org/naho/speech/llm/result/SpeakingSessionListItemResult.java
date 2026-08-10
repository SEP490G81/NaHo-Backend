package org.naho.speech.llm.result;

import java.time.Instant;

/**
 * Item trong danh sách lịch sử các buổi nói AI 1-1 của user.
 */
public record SpeakingSessionListItemResult(
        Long id,
        String sessionCode,
        String topic,
        Long personaId,
        String marugotoLevel,
        String formalityLevel,
        int overallScore,
        String jlptEstimate,
        int totalTurns,
        int durationSeconds,
        Instant startedAt,
        Instant endedAt,
        String status
) {
}
