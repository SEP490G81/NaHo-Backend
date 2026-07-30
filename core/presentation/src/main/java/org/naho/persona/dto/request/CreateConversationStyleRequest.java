package org.naho.persona.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record CreateConversationStyleRequest(
        String description,
        @NotBlank String prompt,
        @NotNull FormalityLevel formalityLevel,
        MarugotoLevel marugotoLevel
) {
}
