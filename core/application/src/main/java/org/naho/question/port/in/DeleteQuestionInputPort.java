package org.naho.question.port.in;

import org.naho.question.command.DeleteQuestionCommand;

public interface DeleteQuestionInputPort {
    void deleteQuestion(DeleteQuestionCommand command);
}
