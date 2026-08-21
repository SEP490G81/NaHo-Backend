package org.naho.persona.command;

import org.naho.persona.type.PersonaStatus;

public record CreatePersonaCommand(
        String name,
        String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId,
        PersonaStatus status,
        String voiceName,
        CreateConversationStyleCommand conversationStyleCommand
) {
}
