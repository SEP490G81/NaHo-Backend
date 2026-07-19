package org.naho.persona.command;

public record UpdatePersonaCommand(
        Long id,
        String name,
        String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId
) {
}
