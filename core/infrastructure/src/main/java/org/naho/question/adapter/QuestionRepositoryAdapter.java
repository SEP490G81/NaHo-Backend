package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.book.repository.ObjectiveJpaRepository;
import org.naho.file.repository.FileJpaRepository;
import org.naho.question.model.Question;
import org.naho.question.port.out.QuestionRepositoryPort;
import org.naho.question.repository.QuestionJpaRepository;
import org.naho.question.type.QuestionStatus;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class QuestionRepositoryAdapter implements QuestionRepositoryPort {

    private final QuestionJpaRepository questionJpaRepository;
    private final ObjectiveJpaRepository objectiveJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final FileJpaRepository fileJpaRepository;

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
    public boolean hasQuestionBeenAnswered(Long questionId) {
        return questionJpaRepository.existsAnswerHistoryByQuestionId(questionId);
    }

    @Override
    public Double getMaxOrderIndexByObjectiveId(Long objectiveId) {
        return questionJpaRepository.getMaxOrderIndexByObjectiveId(objectiveId);
    }

    @Override
    public boolean existsByObjectiveIdAndTitle(Long objectiveId, String title) {
        return questionJpaRepository.existsByObjectiveIdAndTitle(objectiveId, title);
    }

    @Override
    public boolean existsByObjectiveIdAndTitleExcludeId(Long objectiveId, String title, Long id) {
        return questionJpaRepository.existsByObjectiveIdAndTitleAndIdNot(objectiveId, title, id);
    }

    @Override
    public void deleteById(Long id) {
        questionJpaRepository.deleteById(id);
    }

    @Override
    public Question save(Question question) {
        org.naho.question.entity.QuestionEntity entity = new org.naho.question.entity.QuestionEntity();

        if (question.getId() != null) {
            entity.setId(question.getId());
        }

        entity.setTitle(question.getTitle());
        entity.setTitleMarkup(question.getTitleMarkup());
        entity.setDescription(question.getDescription());
        entity.setDescriptionMarkup(question.getDescriptionMarkup());
        entity.setOrderIndex(question.getOrderIndex());
        entity.setStatus(question.getStatus());

        if (question.getObjectiveId() != null) {
            entity.setObjective(objectiveJpaRepository.getReferenceById(question.getObjectiveId()));
        }

        if (question.getUserId() != null) {
            entity.setUser(userJpaRepository.getReferenceById(question.getUserId()));
        }

        if (question.getQuestionAudioFileId() != null) {
            entity.setQuestionAudioFile(fileJpaRepository.getReferenceById(question.getQuestionAudioFileId()));
        }

        org.naho.question.entity.QuestionEntity savedEntity = questionJpaRepository.save(entity);

        return Question.builder()
                .id(savedEntity.getId())
                .questionAudioFileId(savedEntity.getQuestionAudioFile() != null ? savedEntity.getQuestionAudioFile().getId() : null)
                .objectiveId(savedEntity.getObjective() != null ? savedEntity.getObjective().getId() : null)
                .userId(savedEntity.getUser() != null ? savedEntity.getUser().getId() : null)
                .title(savedEntity.getTitle())
                .titleMarkup(savedEntity.getTitleMarkup())
                .description(savedEntity.getDescription())
                .descriptionMarkup(savedEntity.getDescriptionMarkup())
                .orderIndex(savedEntity.getOrderIndex())
                .status(savedEntity.getStatus())
                .build();
    }

    @Override
    public Optional<Question> findById(Long id) {
        return questionJpaRepository.findById(id).map(entity -> Question.builder()
                .id(entity.getId())
                .questionAudioFileId(entity.getQuestionAudioFile() != null ? entity.getQuestionAudioFile().getId() : null)
                .objectiveId(entity.getObjective() != null ? entity.getObjective().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .title(entity.getTitle())
                .titleMarkup(entity.getTitleMarkup())
                .description(entity.getDescription())
                .descriptionMarkup(entity.getDescriptionMarkup())
                .orderIndex(entity.getOrderIndex())
                .status(entity.getStatus())
                .build());
    }

    @Override
    public List<Question> findByObjectiveId (Long objectiveId){
        return questionJpaRepository.findByObjectiveId(objectiveId).stream()
                              .map(entity -> Question.builder()
                                      .id(entity.getId())
                                      .questionAudioFileId(entity.getQuestionAudioFile() != null ? entity.getQuestionAudioFile().getId() : null)
                                      .objectiveId(entity.getObjective() != null ? entity.getObjective().getId() : null)
                                      .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                                      .title(entity.getTitle())
                                      .titleMarkup(entity.getTitleMarkup())
                                      .description(entity.getDescription())
                                      .descriptionMarkup(entity.getDescriptionMarkup())
                                      .orderIndex(entity.getOrderIndex())
                                      .status(entity.getStatus())
                                      .build()).toList();
    }
}
