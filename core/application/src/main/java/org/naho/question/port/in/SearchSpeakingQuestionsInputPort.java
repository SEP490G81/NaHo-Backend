package org.naho.question.port.in;

import org.naho.question.command.SearchSpeakingQuestionsCommand;
import org.naho.question.result.SearchSpeakingQuestionsResult;

public interface SearchSpeakingQuestionsInputPort {
    SearchSpeakingQuestionsResult searchSpeakingQuestions(SearchSpeakingQuestionsCommand command);
}
