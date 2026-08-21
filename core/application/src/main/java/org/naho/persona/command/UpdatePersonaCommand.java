package org.naho.persona.command;

import org.naho.persona.type.PersonaStatus;

public record UpdatePersonaCommand(
        Long id,
        String name,
        String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId,
        PersonaStatus status,
        String voiceName,
        UpdateConversationStyleCommand conversationStyleCommand
) {
}
