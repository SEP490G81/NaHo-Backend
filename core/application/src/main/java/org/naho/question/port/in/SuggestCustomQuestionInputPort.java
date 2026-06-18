package org.naho.question.port.in;

import org.naho.question.command.SuggestCustomQuestionCommand;
import org.naho.question.result.SuggestCustomQuestionResult;

public interface SuggestCustomQuestionInputPort {
    SuggestCustomQuestionResult suggestCustomQuestion(SuggestCustomQuestionCommand command);
}
