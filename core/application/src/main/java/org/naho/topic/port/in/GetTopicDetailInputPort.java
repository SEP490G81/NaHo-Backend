package org.naho.topic.port.in;

import org.naho.topic.command.GetTopicDetailCommand;
import org.naho.topic.result.TopicDetailResult;

public interface GetTopicDetailInputPort {
    TopicDetailResult getTopicDetail(GetTopicDetailCommand command);
}
