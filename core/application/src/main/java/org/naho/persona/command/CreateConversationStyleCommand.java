package org.naho.persona.command;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record CreateConversationStyleCommand(
        String description,
        String prompt,
        FormalityLevel formalityLevel,
        MarugotoLevel marugotoLevel
) {
}
