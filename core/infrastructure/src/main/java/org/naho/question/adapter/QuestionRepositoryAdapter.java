package org.naho.question.adapter;

import org.naho.question.port.out.QuestionRepositoryPort;
import org.naho.question.repository.QuestionJpaRepository;
import org.naho.topic.model.Question;
import org.naho.topic.type.QuestionStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

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

    @Override
    public Optional<Question> findById(Long id) {
        return questionJpaRepository.findById(id).map(entity -> Question.builder()
                .id(entity.getId())
                .questionAudioFileId(entity.getQuestionAudioFile() != null ? entity.getQuestionAudioFile().getId() : null)
                .topicId(entity.getTopic() != null ? entity.getTopic().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .title(entity.getTitle())
                .titleMarkup(entity.getTitleMarkup())
                .description(entity.getDescription())
                .descriptionMarkup(entity.getDescriptionMarkup())
                .orderIndex(entity.getOrderIndex())
                .status(entity.getStatus())
                .build());
    }
}
