package org.naho.speech.topic.port.in;

import org.naho.speech.topic.command.CreateTopicCommand;
import org.naho.speech.topic.result.CreateTopicResult;

public interface CreateTopicInputPort {
    CreateTopicResult createTopic(CreateTopicCommand command);
}
