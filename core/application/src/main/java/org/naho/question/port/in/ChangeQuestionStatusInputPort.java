package org.naho.question.port.in;

import org.naho.question.command.ChangeQuestionStatusCommand;

public interface ChangeQuestionStatusInputPort {
    void changeQuestionStatus(ChangeQuestionStatusCommand command);
}
