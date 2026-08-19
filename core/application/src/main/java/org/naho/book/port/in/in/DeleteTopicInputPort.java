package org.naho.book.port.in.in;

import org.naho.book.command.DeleteTopicCommand;

public interface DeleteTopicInputPort {
    void deleteTopic(DeleteTopicCommand command);
}
