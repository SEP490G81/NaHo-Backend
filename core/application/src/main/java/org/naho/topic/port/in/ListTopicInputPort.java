package org.naho.topic.port.in;

import org.naho.topic.command.ListTopicCommand;
import org.naho.topic.result.TopicListResult;

public interface ListTopicInputPort {
    TopicListResult listTopics(ListTopicCommand query);
}
