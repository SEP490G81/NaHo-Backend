package org.naho.speech.llm.command;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record StartSpeakingConversationWithAICommand(
        Long userId,
        int personaId,
        FormalityLevel formalityLevelOverride,
        MarugotoLevel marugotoLevelOverride
) {
    public StartSpeakingConversationWithAICommand(Long userId, int personaId) {
        this(userId, personaId, null, null);
    }
}
