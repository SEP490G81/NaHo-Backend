package org.naho.persona.dto.response;

import org.naho.file.dto.response.FileResponse;
import org.naho.persona.type.PersonaStatus;
import org.naho.user.type.Gender;

public record PersonaResponse(
        Long id,
        String name,
        String prompt,
        FileResponse avatarFile,
        ConversationStyleResponse suggestedConversationStyle,
        PersonaStatus status,
        String voiceName,
        Gender gender
) {
}
