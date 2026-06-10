package org.naho.speech.topic.port.in;

import org.naho.speech.topic.command.GetTopicDetailCommand;
import org.naho.speech.topic.result.TopicDetailResult;

public interface GetTopicDetailInputPort {
    TopicDetailResult getTopicDetail(GetTopicDetailCommand command);
}
