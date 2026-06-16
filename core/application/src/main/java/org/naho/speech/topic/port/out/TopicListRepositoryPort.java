package org.naho.speech.topic.port.out;

import org.naho.speech.topic.command.ListTopicCommand;
import org.naho.speech.topic.result.TopicListItemResult;

import java.util.List;

public interface TopicListRepositoryPort {
    long countTopics(ListTopicCommand query);
    List<TopicListItemResult> findTopics(ListTopicCommand query);
}
