package org.naho.speech.topic.port.in;

import org.naho.speech.topic.command.UpdateTopicCommand;
import org.naho.speech.topic.result.TopicDetailResult;

public interface UpdateTopicInputPort {
    TopicDetailResult updateTopic(UpdateTopicCommand command);
}
