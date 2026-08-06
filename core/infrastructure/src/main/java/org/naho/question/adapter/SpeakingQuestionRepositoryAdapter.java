package org.naho.question.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.repository.FileJpaRepository;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.mapper.SpeakingQuestionEntityMapper;
import org.naho.question.model.SpeakingQuestion;
import org.naho.question.port.out.SpeakingQuestionRepositoryPort;
import org.naho.question.repository.SpeakingQuestionJpaRepository;
import org.naho.question.type.QuestionStatus;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpeakingQuestionRepositoryAdapter implements SpeakingQuestionRepositoryPort {

    private final SpeakingQuestionJpaRepository speakingQuestionJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final FileJpaRepository fileJpaRepository;
    private final SpeakingQuestionEntityMapper speakingQuestionEntityMapper;

    @Override
    public void deleteSpeakingQuestionsByTopicId(Long topicId) {
        speakingQuestionJpaRepository.deleteAllByTopicId(topicId);
    }

    @Override
    public void updateSpeakingQuestionsStatusByTopicId(Long topicId, QuestionStatus status) {
        speakingQuestionJpaRepository.updateStatusByTopicId(topicId, status);
    }

    @Override
    public boolean hasAnySpeakingQuestionBeenAnsweredInTopic(Long topicId) {
        return speakingQuestionJpaRepository.existsAnswerHistoryByTopicId(topicId);
    }

    @Override
    public boolean hasSpeakingQuestionBeenAnswered(Long questionId) {
        return speakingQuestionJpaRepository.existsAnswerHistoryByQuestionId(questionId);
    }


    @Override
    public void deleteById(Long id) {
        speakingQuestionJpaRepository.deleteById(id);
    }

    @Override
    public SpeakingQuestion save(SpeakingQuestion speakingQuestion) {
        SpeakingQuestionEntity entity = new SpeakingQuestionEntity();

        if (speakingQuestion.getId() != null) {
            entity.setId(speakingQuestion.getId());
        }

        entity.setJapaneseName(speakingQuestion.getJapaneseName());
        entity.setJapaneseNameMarkup(speakingQuestion.getJapaneseNameMarkup());
        entity.setVietnameseName(speakingQuestion.getVietnameseName());
        entity.setDescription(speakingQuestion.getDescription());
        entity.setDescriptionMarkup(speakingQuestion.getDescriptionMarkup());
        entity.setJapaneseSampleAnswer(speakingQuestion.getJapaneseSampleAnswer());
        entity.setJapaneseSampleAnswerMarkup(speakingQuestion.getJapaneseSampleAnswerMarkup());
        entity.setVietnameseSampleAnswer(speakingQuestion.getVietnameseSampleAnswer());
        entity.setStatus(speakingQuestion.getStatus());


        if (speakingQuestion.getUserId() != null) {
            entity.setUser(userJpaRepository.getReferenceById(speakingQuestion.getUserId()));
        }

        if (speakingQuestion.getSpeakingQuestionAudioFileId() != null) {
            entity.setSpeakingQuestionAudioFile(fileJpaRepository.getReferenceById(speakingQuestion.getSpeakingQuestionAudioFileId()));
        }

        SpeakingQuestionEntity savedEntity = speakingQuestionJpaRepository.save(entity);

        return speakingQuestionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<SpeakingQuestion> findById(Long id) {
        return speakingQuestionJpaRepository
                .findById(id)
                .map(speakingQuestionEntityMapper::entityToDomain);
    }

    @Override
    public List<SpeakingQuestion> findByObjectiveId(Long objectiveId) {
        return speakingQuestionJpaRepository.findByObjectiveId(objectiveId).stream()
                .map(speakingQuestionEntityMapper::entityToDomain)
                .toList();
    }
}
