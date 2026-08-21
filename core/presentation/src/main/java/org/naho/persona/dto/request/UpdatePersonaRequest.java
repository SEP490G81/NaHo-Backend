package org.naho.persona.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.naho.persona.type.PersonaStatus;

public record UpdatePersonaRequest(
        @NotBlank String name,
        @NotBlank String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId,
        PersonaStatus status,
        String voiceName,
        UpdateConversationStyleRequest conversationStyle
) {
}
