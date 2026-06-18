package org.naho.question.port.in;

import org.naho.question.command.UpdateQuestionCommand;
import org.naho.question.result.UpdateQuestionResult;

public interface UpdateQuestionInputPort {
    UpdateQuestionResult updateQuestion(UpdateQuestionCommand command);
}
