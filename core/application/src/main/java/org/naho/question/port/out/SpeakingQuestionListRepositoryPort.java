package org.naho.question.port.out;

import org.naho.question.command.SearchSpeakingQuestionsCommand;
import org.naho.question.result.SpeakingQuestionListItemResult;

import java.util.List;

public interface SpeakingQuestionListRepositoryPort {
    long countSpeakingQuestions(SearchSpeakingQuestionsCommand query);

    List<SpeakingQuestionListItemResult> findSpeakingQuestions(SearchSpeakingQuestionsCommand query);
}
