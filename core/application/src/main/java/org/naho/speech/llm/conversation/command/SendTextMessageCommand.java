package org.naho.speech.llm.conversation.command;

import org.naho.i18n.message.llm.LlmTitleMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;

public record SendTextMessageCommand(
        String sessionCode,
        String userMessage
) {
    public SendTextMessageCommand {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmTitleMessageKey.LLM_SESSION_CODE_INVALID_TITLE
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
