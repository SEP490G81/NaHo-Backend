package org.naho.question.port.out;

import org.naho.topic.type.QuestionStatus;

public interface QuestionRepositoryPort {
    void deleteQuestionsByTopicId(Long topicId);

    void updateQuestionsStatusByTopicId(Long topicId, QuestionStatus status);

    boolean hasAnyQuestionBeenAnsweredInTopic(Long topicId);
}
