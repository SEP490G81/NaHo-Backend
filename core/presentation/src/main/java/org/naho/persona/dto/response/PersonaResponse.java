package org.naho.persona.dto.response;

import org.naho.persona.type.PersonaStatus;

public record PersonaResponse(
        Long id,
        String name,
        String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId,
        ConversationStyleResponse conversationStyle,
        PersonaStatus status
) {
}



