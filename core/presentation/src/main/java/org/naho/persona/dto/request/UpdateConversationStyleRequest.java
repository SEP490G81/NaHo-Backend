package org.naho.persona.dto.request;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record UpdateConversationStyleRequest(
        Long id,
        String description,
        String prompt,
        FormalityLevel formalityLevel,
        MarugotoLevel marugotoLevel
) {
}
