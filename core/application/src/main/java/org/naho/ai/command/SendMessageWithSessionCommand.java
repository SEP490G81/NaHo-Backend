package org.naho.ai.command;

import org.naho.ai.constant.AiApplicationMessageKey;
import org.naho.ai.exception.AiApplicationError;
import org.naho.shared.exception.ApplicationException;

public record SendMessageWithSessionCommand(
        String sessionId,
        String userMessage
) {
    public SendMessageWithSessionCommand {
        if (sessionId == null || sessionId.isBlank()){
            throw new ApplicationException(
                    AiApplicationError.AI_SESSION_ID_INVALID,
                    AiApplicationMessageKey.AI_SESSION_ID_INVALID_TITLE
            );
        }
        if (userMessage == null || userMessage.isBlank()){
            throw new ApplicationException(
                    AiApplicationError.AI_USER_MESSAGE_INVALID,
                    AiApplicationMessageKey.AI_USER_MESSAGE_INVALID_TITLE
            );
        }
    }
}
