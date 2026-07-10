package org.naho.book.port.in;

import org.naho.book.command.ListTopicCommand;
import org.naho.book.result.TopicListResult;

public interface ListTopicInputPort {
    TopicListResult listTopics(ListTopicCommand query);
}
