package org.naho.topic.port.in;

import org.naho.topic.command.DeleteTopicCommand;

public interface DeleteTopicInputPort {
    void deleteTopic(DeleteTopicCommand command);
}
