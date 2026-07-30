package org.naho.persona.command;

public record CreatePersonaCommand(
        String name,
        String prompt,
        Long avatarFileId,
        Long suggestedConversationStyleId,
        CreateConversationStyleCommand conversationStyleCommand
) {
}
