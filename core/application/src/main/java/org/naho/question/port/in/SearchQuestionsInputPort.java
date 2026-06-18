package org.naho.question.port.in;

import org.naho.question.command.SearchQuestionsCommand;
import org.naho.question.result.SearchQuestionsResult;

public interface SearchQuestionsInputPort {
    SearchQuestionsResult searchQuestions(SearchQuestionsCommand command);
}
