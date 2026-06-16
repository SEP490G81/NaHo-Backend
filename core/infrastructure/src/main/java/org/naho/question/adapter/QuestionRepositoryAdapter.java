package org.naho.question.adapter;

import org.naho.question.port.out.QuestionRepositoryPort;
import org.naho.question.repository.QuestionJpaRepository;
import org.naho.topic.type.QuestionStatus;
import org.springframework.stereotype.Component;

@Component
public class QuestionRepositoryAdapter implements QuestionRepositoryPort {

    private final QuestionJpaRepository questionJpaRepository;

    public QuestionRepositoryAdapter(QuestionJpaRepository questionJpaRepository) {
        this.questionJpaRepository = questionJpaRepository;
    }

    @Override
    public void deleteQuestionsByTopicId(Long topicId) {
        questionJpaRepository.deleteAllByTopicId(topicId);
    }

    @Override
    public void updateQuestionsStatusByTopicId(Long topicId, QuestionStatus status) {
        questionJpaRepository.updateStatusByTopicId(topicId, status);
    }

    @Override
    public boolean hasAnyQuestionBeenAnsweredInTopic(Long topicId) {
        return questionJpaRepository.existsAnswerHistoryByTopicId(topicId);
    }
}
