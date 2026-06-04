package org.naho.speech.topic.port.in;

import org.naho.speech.topic.query.ListTopicQuery;
import org.naho.speech.topic.result.TopicListResult;

public interface ListTopicUseCasePort {
    TopicListResult listTopics(ListTopicQuery query);
}
