package org.naho.speech.llm.conversation.result;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;

/**
 * Item trong danh sách lịch sử các buổi nói AI 1-1 của user.
 */
public record SpeakingSessionListItemResult(
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
