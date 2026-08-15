package org.naho.book.port.in.in;

import org.naho.book.command.CreateTopicCommand;
import org.naho.book.result.CreateTopicResult;

public interface CreateTopicInputPort {
    CreateTopicResult createTopic(CreateTopicCommand command);
}
