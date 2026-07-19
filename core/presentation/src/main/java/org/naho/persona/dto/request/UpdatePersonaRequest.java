package org.naho.persona.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdatePersonaRequest(
        @NotBlank String name,
        @NotBlank String prompt,
        Long avatarFileId,
        @NotNull Long suggestedConversationStyleId
) {}
