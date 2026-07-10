package org.naho.book.port.in;

import org.naho.book.command.UpdateTopicCommand;
import org.naho.book.result.TopicDetailResult;

public interface UpdateTopicInputPort {
    TopicDetailResult updateTopic(UpdateTopicCommand command);
}
