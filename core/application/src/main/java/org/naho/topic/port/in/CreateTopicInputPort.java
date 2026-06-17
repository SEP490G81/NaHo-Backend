package org.naho.topic.port.in;

import org.naho.topic.command.CreateTopicCommand;
import org.naho.topic.result.CreateTopicResult;

public interface CreateTopicInputPort {
    CreateTopicResult createTopic(CreateTopicCommand command);
}
