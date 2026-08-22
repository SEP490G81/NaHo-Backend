package org.naho.persona.command;

import org.naho.persona.type.PersonaStatus;
import org.naho.user.type.Gender;

public record UpdatePersonaCommand(
        Long id,
        String name,
        String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId,
        PersonaStatus status,
        String voiceName,
        Gender gender,
        UpdateConversationStyleCommand conversationStyleCommand
) {
}
