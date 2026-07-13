package org.naho.persona.dto.response;

public record PersonaResponse(
        Long id,
        String name,
        String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId
) {}
