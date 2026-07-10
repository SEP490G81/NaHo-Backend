package org.naho.book.port.out;

import org.naho.book.command.ListTopicCommand;
import org.naho.book.result.TopicListItemResult;

import java.util.List;

public interface TopicListRepositoryPort {
    long countTopics(ListTopicCommand query);

    List<TopicListItemResult> findTopics(ListTopicCommand query);
}
