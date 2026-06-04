package org.naho.speech.llm.command;

import org.naho.i18n.message.llm.LlmTitleMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.exception.LlmApplicationError;

public record SendMessageWithSessionCommand(
        String sessionId,
        String userMessage
) {
    public SendMessageWithSessionCommand {
        if (sessionId == null || sessionId.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_ID_INVALID,
                    LlmTitleMessageKey.LLM_SESSION_ID_INVALID_TITLE
            );
        }
        if (userMessage == null || userMessage.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_USER_MESSAGE_INVALID,
                    LlmTitleMessageKey.LLM_USER_MESSAGE_INVALID_TITLE
            );
        }
    }
}
