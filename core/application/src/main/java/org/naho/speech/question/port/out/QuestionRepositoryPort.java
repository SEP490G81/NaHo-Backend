package org.naho.speech.question.port.out;

import org.naho.topic.model.Question;
import org.naho.topic.type.QuestionStatus;

import java.util.Optional;

public interface QuestionRepositoryPort {
    void deleteQuestionsByTopicId(Long topicId);

    void updateQuestionsStatusByTopicId(Long topicId, QuestionStatus status);

    boolean hasAnyQuestionBeenAnsweredInTopic(Long topicId);
    Optional<Question> findById(Long id);
}
