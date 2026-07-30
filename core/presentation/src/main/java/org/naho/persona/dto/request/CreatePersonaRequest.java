package org.naho.persona.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreatePersonaRequest(
        @NotBlank String name,
        @NotBlank String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId,
        CreateConversationStyleRequest conversationStyle
) {
}
