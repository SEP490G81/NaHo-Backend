package org.naho.topic.port.in;

import org.naho.topic.command.UpdateTopicCommand;
import org.naho.topic.result.TopicDetailResult;

public interface UpdateTopicInputPort {
    TopicDetailResult updateTopic(UpdateTopicCommand command);
}
