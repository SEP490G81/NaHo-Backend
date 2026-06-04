package org.naho.speech.topic.port.in;

import org.naho.speech.topic.command.CreateTopicCommand;
import org.naho.speech.topic.result.TopicResult;

public interface CreateTopicInputPort {
    TopicResult createTopic(CreateTopicCommand command);
}
