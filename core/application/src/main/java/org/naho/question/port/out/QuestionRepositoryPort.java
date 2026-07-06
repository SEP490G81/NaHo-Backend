package org.naho.question.port.out;

import org.naho.question.model.Question;
import org.naho.question.type.QuestionStatus;

import java.util.Optional;

public interface QuestionRepositoryPort {
    void deleteQuestionsByTopicId(Long topicId);

    void updateQuestionsStatusByTopicId(Long topicId, QuestionStatus status);

    boolean hasAnyQuestionBeenAnsweredInTopic(Long topicId);

    boolean hasQuestionBeenAnswered(Long questionId);

    Optional<Question> findById(Long id);

    Question save(Question question);

    void deleteById(Long id);

    Double getMaxOrderIndexByObjectiveId(Long objectiveId);

    boolean existsByObjectiveIdAndTitle(Long objectiveId, String title);

    boolean existsByObjectiveIdAndTitleExcludeId(Long objectiveId, String title, Long id);
}

