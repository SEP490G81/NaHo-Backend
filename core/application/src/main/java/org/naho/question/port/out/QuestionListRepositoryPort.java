package org.naho.question.port.out;

import org.naho.question.command.SearchQuestionsCommand;
import org.naho.question.result.QuestionListItemResult;

import java.util.List;

public interface QuestionListRepositoryPort {
    long countQuestions(SearchQuestionsCommand query);
    List<QuestionListItemResult> findQuestions(SearchQuestionsCommand query);
}
