package org.naho.persona.dto.response;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record ConversationStyleResponse(
        Long id,
        String description,
        String prompt,
        FormalityLevel formalityLevel,
        MarugotoLevel marugotoLevel
) {
}
