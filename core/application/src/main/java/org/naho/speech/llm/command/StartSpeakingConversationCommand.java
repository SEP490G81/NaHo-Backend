package org.naho.speech.llm.command;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record StartSpeakingConversationCommand(
        Long userId,
        Long personaId,
        FormalityLevel formalityLevelOverride,
        MarugotoLevel marugotoLevelOverride
) {
}
