package org.naho.speech.topic.port.in;

import org.naho.speech.topic.command.ListTopicCommand;
import org.naho.speech.topic.result.TopicListResult;

public interface ListTopicUseCasePort {
    TopicListResult listTopics(ListTopicCommand query);
}
