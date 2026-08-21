package org.naho.question.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.naho.file.entity.FileEntity;
import org.naho.file.mapper.FileEntityMapper;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.i18n.message.file.FileDetailMessageKey;
import org.naho.i18n.message.question.SpeakingQuestionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.question.entity.AnswerHistoryEntity;
import org.naho.question.entity.SpeakingQuestionEntity;
import org.naho.question.mapper.AnswerHistoryEntityMapper;
import org.naho.question.port.out.AnswerHistoryRepositoryPort;
import org.naho.question.repository.AnswerHistoryJpaRepository;
import org.naho.question.repository.SpeakingQuestionJpaRepository;
import org.naho.shared.exception.CommonErrorCode;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.model.AnswerHistory;
import org.naho.speech.azure.repository.SpeechAssessmentJpaRepository;
import org.naho.speech.azure.repository.WordAssessmentJpaRepository;
import org.naho.speech.llm.question.repository.AiFeedbackJpaRepository;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AnswerHistoryRepositoryAdapter implements AnswerHistoryRepositoryPort {

    private final AnswerHistoryJpaRepository answerHistoryJpaRepository;
    private final SpeechAssessmentJpaRepository speechAssessmentJpaRepository;
    private final WordAssessmentJpaRepository wordAssessmentJpaRepository;

    private final UserJpaRepository userJpaRepository;
    private final SpeakingQuestionJpaRepository questionJpaRepository;
    private final FileJpaRepository fileJpaRepository;
    private final FileStorageServicePort fileStorageServicePort;
    private final AnswerHistoryEntityMapper answerHistoryEntityMapper;
    private final AiFeedbackJpaRepository aiFeedbackJpaRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final FileEntityMapper fileEntityMapper;

    @Override
    public AnswerHistory save(AnswerHistory answerHistory) {
        if (answerHistory.getUserId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (answerHistory.getSpeakingQuestionId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_ID_NULL
            );
        }

        UserEntity user = userJpaRepository.getReferenceById(answerHistory.getUserId());
        SpeakingQuestionEntity question = questionJpaRepository.getReferenceById(answerHistory.getSpeakingQuestionId());

        AnswerHistoryEntity entity = AnswerHistoryEntity.builder()
                .id(answerHistory.getId())
                .user(user)
                .speakingQuestion(question)
                .duration(answerHistory.getDuration())
                .build();

        if (answerHistory.getAudioFileId() != null) {
            FileEntity file = fileJpaRepository.getReferenceById(answerHistory.getAudioFileId());
            entity.setAudioFile(file);
        }

        AnswerHistoryEntity saved = answerHistoryJpaRepository.save(entity);
        return answerHistoryEntityMapper.entityToDomain(saved);
    }

    @Override
    public AnswerHistory createNew(AnswerHistory answerHistory) {
        if (answerHistory.getUserId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (answerHistory.getSpeakingQuestionId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    SpeakingQuestionDetailMessageKey.SPEAKING_QUESTION_ID_NULL
            );
        }

        if (answerHistory.getSpeechAssessmentId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    SpeakingQuestionDetailMessageKey.SPEECH_ASSESSMENT_ID_NULL
            );
        }

        if (answerHistory.getAiFeedbackId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    SpeakingQuestionDetailMessageKey.AI_FEEDBACK_ID_NULL
            );
        }

        if (answerHistory.getAudioFileId() == null) {
            throw new InfrastructureException(
                    CommonErrorCode.COMMON_INVALID_REQUEST,
                    FileDetailMessageKey.FILE_ID_NULL
            );
        }

        AnswerHistoryEntity entity = AnswerHistoryEntity.builder()
                .user(userJpaRepository.getReferenceById(answerHistory.getUserId()))
                .speakingQuestion(questionJpaRepository.getReferenceById(answerHistory.getSpeakingQuestionId()))
                .speechAssessment(speechAssessmentJpaRepository.getReferenceById(answerHistory.getSpeechAssessmentId()))
                .aiFeedback(aiFeedbackJpaRepository.getReferenceById(answerHistory.getAiFeedbackId()))
                .audioFile(fileJpaRepository.getReferenceById(answerHistory.getAudioFileId()))
                .duration(answerHistory.getDuration())
                .overallScore(answerHistory.getOverallScore())
                .build();
        AnswerHistoryEntity saved = answerHistoryJpaRepository.save(entity);
        return answerHistoryEntityMapper.entityToDomain(saved);
    }

    @Override
    public Optional<AnswerHistory> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return answerHistoryJpaRepository
                .findById(id)
                .map(answerHistoryEntityMapper::entityToDomain);
    }
}
