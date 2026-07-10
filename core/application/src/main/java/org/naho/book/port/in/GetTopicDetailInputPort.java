package org.naho.book.port.in;

import org.naho.book.command.GetTopicDetailCommand;
import org.naho.book.result.TopicDetailResult;

public interface GetTopicDetailInputPort {
    TopicDetailResult getTopicDetail(GetTopicDetailCommand command);
}
