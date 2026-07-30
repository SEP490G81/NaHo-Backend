package org.naho.persona.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdatePersonaRequest(
        @NotBlank String name,
        @NotBlank String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId,
        UpdateConversationStyleRequest conversationStyle
) {
}
