package org.naho.speech.llm.command;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record StartSpeakingConversationWithAICommand(
        int personaId,
        FormalityLevel formalityLevelOverride,
        MarugotoLevel marugotoLevelOverride
) {
    public StartSpeakingConversationWithAICommand(int personaId) {
        this(personaId, null, null);
    }
}
