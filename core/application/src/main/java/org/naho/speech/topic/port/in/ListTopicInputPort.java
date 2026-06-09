package org.naho.speech.topic.port.in;

import org.naho.speech.topic.command.ListTopicCommand;
import org.naho.speech.topic.result.TopicListResult;

public interface ListTopicInputPort {
    TopicListResult listTopics(ListTopicCommand query);
}
