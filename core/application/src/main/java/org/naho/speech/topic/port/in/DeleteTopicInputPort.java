package org.naho.speech.topic.port.in;

import org.naho.speech.topic.command.DeleteTopicCommand;

public interface DeleteTopicInputPort {
    void deleteTopic(DeleteTopicCommand command);
}
