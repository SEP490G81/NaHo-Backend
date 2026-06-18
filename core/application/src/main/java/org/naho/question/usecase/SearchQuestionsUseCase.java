package org.naho.question.usecase;

import org.naho.question.command.SearchQuestionsCommand;
import org.naho.question.port.in.SearchQuestionsInputPort;
import org.naho.question.port.out.QuestionListRepositoryPort;
import org.naho.question.result.QuestionListItemResult;
import org.naho.question.result.SearchQuestionsResult;

import java.util.List;

public class SearchQuestionsUseCase implements SearchQuestionsInputPort {

    private final QuestionListRepositoryPort questionListRepositoryPort;

    public SearchQuestionsUseCase(QuestionListRepositoryPort questionListRepositoryPort) {
        this.questionListRepositoryPort = questionListRepositoryPort;
    }

    @Override
    public SearchQuestionsResult searchQuestions(SearchQuestionsCommand command) {
        long totalElements = questionListRepositoryPort.countQuestions(command);

        List<QuestionListItemResult> items = List.of();
        int totalPages = 0;

        if (totalElements > 0) {
            items = questionListRepositoryPort.findQuestions(command);
            totalPages = (int) Math.ceil((double) totalElements / command.size());
        }

        return new SearchQuestionsResult(
                items,
                command.page(),
                command.size(),
                totalPages,
                totalElements
        );
    }
}
