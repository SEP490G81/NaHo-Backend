package org.naho.speech.llm.conversation.command;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record StartSpeakingConversationCommand(
        Long userId,
        Long personaId,
        FormalityLevel formalityLevel,
        MarugotoLevel marugotoLevel
) {
}
