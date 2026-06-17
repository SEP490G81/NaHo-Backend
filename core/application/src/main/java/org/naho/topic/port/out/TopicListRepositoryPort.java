package org.naho.topic.port.out;

import org.naho.topic.command.ListTopicCommand;
import org.naho.topic.result.TopicListItemResult;

import java.util.List;

public interface TopicListRepositoryPort {
    long countTopics(ListTopicCommand query);
    List<TopicListItemResult> findTopics(ListTopicCommand query);
}
