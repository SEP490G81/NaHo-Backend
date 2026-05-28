package org.naho.ai.command;

import org.naho.ai.constant.AiApplicationMessageKey;
import org.naho.ai.exception.AiApplicationError;
import org.naho.shared.exception.ApplicationException;

public record StartSpeakingCommand(String topic) {
    public StartSpeakingCommand {
        if(topic == null || topic.isBlank()){
            throw new ApplicationException(
                    AiApplicationError.AI_TOPIC_INVALID,
                    AiApplicationMessageKey.AI_TOPIC_INVALID_TITLE
            );
        }
    }
}
