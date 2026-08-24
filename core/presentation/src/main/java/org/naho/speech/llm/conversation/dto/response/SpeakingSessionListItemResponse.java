package org.naho.speech.llm.conversation.dto.response;

import org.naho.persona.dto.response.PersonaResponse;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;

public record SpeakingSessionListItemResponse(
        Long id,
        String sessionCode,
        Long userId,
        PersonaResponse persona,
        String topic,
        String voiceName,
        MarugotoLevel marugotoLevel,
        FormalityLevel formalityLevel,
        Integer totalTurns,
        SpeakingSessionStatus status,
        Instant startedAt,
        Instant endedAt
) {
}
