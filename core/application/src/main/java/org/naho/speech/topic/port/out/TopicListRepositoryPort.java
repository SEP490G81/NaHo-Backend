package org.naho.speech.topic.port.out;

import org.naho.speech.topic.query.ListTopicQuery;
import org.naho.speech.topic.result.TopicListItemResult;

import java.util.List;

public interface TopicListRepositoryPort {
    long countTopics(ListTopicQuery query);
    List<TopicListItemResult> findTopics(ListTopicQuery query);
}
