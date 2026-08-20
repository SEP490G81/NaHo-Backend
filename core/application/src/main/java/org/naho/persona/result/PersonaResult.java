package org.naho.persona.result;

import org.naho.file.result.FileResult;
import org.naho.persona.model.ConversationStyle;
import org.naho.persona.type.PersonaStatus;

public record PersonaResult(
        Long id,
        String name,
        String prompt,
        Long avatarFileId,
        FileResult avatarFile,
        Long suggestedConversationStyleId,
        ConversationStyle conversationStyle,
        PersonaStatus status
) {
}
