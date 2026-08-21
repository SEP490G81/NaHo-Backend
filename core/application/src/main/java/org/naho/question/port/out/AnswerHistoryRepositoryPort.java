package org.naho.question.port.out;

import org.naho.speech.azure.model.AnswerHistory;

import java.util.List;
import java.util.Optional;

public interface AnswerHistoryRepositoryPort {
    AnswerHistory save(AnswerHistory answerHistory);

    AnswerHistory createNew(AnswerHistory answerHistory);

    Optional<AnswerHistory> findById(Long id);

    List<AnswerHistory> findAllBySpeakingQuestionIdAndUserId(Long speakingQuestionId, Long userId);
}
