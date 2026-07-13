package org.naho.question.usecase;

import org.naho.question.command.SearchSpeakingQuestionsCommand;
import org.naho.question.port.in.SearchSpeakingQuestionsInputPort;
import org.naho.question.port.out.SpeakingQuestionListRepositoryPort;
import org.naho.question.result.SearchSpeakingQuestionsResult;
import org.naho.question.result.SpeakingQuestionListItemResult;

import java.util.List;

public class SearchSpeakingQuestionsUseCase implements SearchSpeakingQuestionsInputPort {

    private final SpeakingQuestionListRepositoryPort speakingQuestionListRepositoryPort;

    public SearchSpeakingQuestionsUseCase(SpeakingQuestionListRepositoryPort speakingQuestionListRepositoryPort) {
        this.speakingQuestionListRepositoryPort = speakingQuestionListRepositoryPort;
    }

    @Override
    public SearchSpeakingQuestionsResult searchSpeakingQuestions(SearchSpeakingQuestionsCommand command) {
        long totalElements = speakingQuestionListRepositoryPort.countSpeakingQuestions(command);

        List<SpeakingQuestionListItemResult> items = List.of();
        int totalPages = 0;

        if (totalElements > 0) {
            items = speakingQuestionListRepositoryPort.findSpeakingQuestions(command);
            totalPages = (int) Math.ceil((double) totalElements / command.size());
        }

        return new SearchSpeakingQuestionsResult(
                items,
                command.page(),
                command.size(),
                totalPages,
                totalElements
        );
    }
}
