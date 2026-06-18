package org.naho.question.port.in;

import org.naho.question.command.CreateQuestionCommand;
import org.naho.question.result.CreateQuestionResult;

public interface CreateQuestionInputPort {
    CreateQuestionResult createQuestion(CreateQuestionCommand command);
}
