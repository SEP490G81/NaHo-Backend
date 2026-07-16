package org.naho.speech.llm.command;

import org.naho.i18n.message.llm.LlmTitleMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.exception.LlmApplicationError;

public record StartSpeakingTopicCommand(String topic) {
    public StartSpeakingTopicCommand {
        if (topic == null || topic.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_TOPIC_INVALID,
                    LlmTitleMessageKey.LLM_TOPIC_INVALID_TITLE
            );
        }
    }
}
