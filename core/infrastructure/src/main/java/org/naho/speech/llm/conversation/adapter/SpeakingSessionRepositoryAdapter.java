package org.naho.speech.llm.conversation.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.file.entity.FileEntity;
import org.naho.file.model.File;
import org.naho.file.repository.FileJpaRepository;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.entity.SpeakingImprovedExpressionEntity;
import org.naho.speech.llm.conversation.entity.SpeakingSessionAssessmentEntity;
import org.naho.speech.llm.conversation.entity.SpeakingSessionEntity;
import org.naho.speech.llm.conversation.entity.SpeakingSessionMessageEntity;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionEntityMapper;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.repository.SpeakingSessionAssessmentJpaRepository;
import org.naho.speech.llm.conversation.repository.SpeakingSessionJpaRepository;
import org.naho.speech.llm.conversation.repository.SpeakingSessionMessageJpaRepository;
import org.naho.speech.llm.conversation.result.SpeakingImprovedExpressionResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.user.exception.UserErrorCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SpeakingSessionRepositoryAdapter implements SpeakingSessionRepositoryPort {

    private final SpeakingSessionJpaRepository sessionJpaRepository;
    private final SpeakingSessionAssessmentJpaRepository assessmentJpaRepository;
    private final SpeakingSessionMessageJpaRepository speakingSessionMessageJpaRepository;
    private final FileJpaRepository fileJpaRepository;
    private final SpeakingSessionEntityMapper speakingSessionEntityMapper;

    @Override
    @Transactional
    public SpeakingSession saveSpeakingSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            MarugotoLevel marugotoLevel,
            FormalityLevel formalityLevel,
            String fullTranscript,
            int totalTurns,
            Double asrConfidence,
            Instant startedAt,
            SpeakingSessionAssessmentResult speakingSessionAssessmentResult
    ) {
        // 1. Lưu/Cập nhật speaking_sessions thành COMPLETED
        SpeakingSessionEntity sessionEntity = sessionJpaRepository.findBySessionCode(sessionCode)
                .orElseGet(() -> SpeakingSessionEntity.builder().sessionCode(sessionCode).build());

        sessionEntity.setUserId(userId);
        sessionEntity.setPersonaId(personaId);
        sessionEntity.setTopic(topic);
        sessionEntity.setMarugotoLevel(marugotoLevel);
        sessionEntity.setFormalityLevel(formalityLevel);
        sessionEntity.setFullTranscript(fullTranscript);
        sessionEntity.setTotalTurns(totalTurns);
        sessionEntity.setAsrConfidence(asrConfidence);
        sessionEntity.setStatus(SpeakingSessionStatus.COMPLETED);
        if (sessionEntity.getStartedAt() == null) {
            sessionEntity.setStartedAt(startedAt != null ? startedAt : Instant.now());
        }
        Instant endedAt = Instant.now();
        sessionEntity.setEndedAt(endedAt);
        if (sessionEntity.getStartedAt() != null) {
            sessionEntity.setDurationSeconds(
                    (int) java.time.Duration.between(sessionEntity.getStartedAt(), endedAt).getSeconds());
        }
        SpeakingSessionEntity savedSession = sessionJpaRepository.save(sessionEntity);

        // 2. Lưu speaking_session_assessments
        SpeakingSessionAssessmentEntity assessmentEntity = SpeakingSessionAssessmentEntity
                .builder()
                .speakingSession(savedSession)
                .overallScore(speakingSessionAssessmentResult.overallScore())
                .jlptEstimate(speakingSessionAssessmentResult.jlptEstimate())
                .fluencyScore(speakingSessionAssessmentResult.fluencyScore())
                .pronunciationScore(speakingSessionAssessmentResult.pronunciationScore())
                .grammarScore(speakingSessionAssessmentResult.grammarScore())
                .vocabularyScore(speakingSessionAssessmentResult.vocabularyScore())
                .interactionScore(speakingSessionAssessmentResult.interactionScore())
                .naturalnessScore(speakingSessionAssessmentResult.naturalnessScore())
                .coherenceScore(speakingSessionAssessmentResult.coherenceScore())
                .summary(speakingSessionAssessmentResult.summary())
                .strengths(speakingSessionAssessmentResult.strengths())
                .weaknesses(speakingSessionAssessmentResult.weaknesses())
                .feedbackFluency(speakingSessionAssessmentResult.feedbackFluency())
                .feedbackPronunciation(speakingSessionAssessmentResult.feedbackPronunciation())
                .feedbackGrammar(speakingSessionAssessmentResult.feedbackGrammar())
                .feedbackVocabulary(speakingSessionAssessmentResult.feedbackVocabulary())
                .feedbackInteraction(speakingSessionAssessmentResult.feedbackInteraction())
                .feedbackNaturalness(speakingSessionAssessmentResult.feedbackNaturalness())
                .feedbackCoherence(speakingSessionAssessmentResult.feedbackCoherence())
                .studyFocusArea(speakingSessionAssessmentResult.studyFocusArea())
                .studyRecommendation(speakingSessionAssessmentResult.studyRecommendation())
                .studyEncouragement(speakingSessionAssessmentResult.studyEncouragement())
                .build();

        SpeakingSessionAssessmentEntity savedAssessment = assessmentJpaRepository.save(assessmentEntity);

        // 3. Lưu speaking_improved_expressions
        if (speakingSessionAssessmentResult.speakingImprovedExpressions() != null && !speakingSessionAssessmentResult.speakingImprovedExpressions().isEmpty()) {
            List<SpeakingImprovedExpressionEntity> expressions = new ArrayList<>();
            for (SpeakingImprovedExpressionResult expr : speakingSessionAssessmentResult.speakingImprovedExpressions()) {
                if (expr.originalText() == null || expr.originalText().isBlank())
                    continue;
                expressions.add(SpeakingImprovedExpressionEntity.builder()
                        .speakingSessionAssessment(savedAssessment)
                        .turnIndex(expr.turnIndex())
                        .originalText(expr.originalText())
                        .improvedText(expr.improvedText() != null ? expr.improvedText() : "")
                        .explanationVietnamese(expr.explanationVietnamese())
                        .build());
            }
            savedAssessment.setSpeakingImprovedExpressions(expressions);
            assessmentJpaRepository.save(savedAssessment);
        }

        return speakingSessionEntityMapper.entityToDomain(savedSession);
    }

    @Override
    @Transactional
    public SpeakingSession initSpeakingSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            String voiceName,
            FormalityLevel formalityLevel,
            MarugotoLevel marugotoLevel
    ) {
        if (userId == null) {
            throw new InfrastructureException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity sessionEntity = SpeakingSessionEntity.builder()
                .sessionCode(sessionCode)
                .userId(userId)
                .personaId(personaId)
                .topic(topic)
                .voiceName(voiceName)
                .marugotoLevel(marugotoLevel)
                .formalityLevel(formalityLevel)
                .totalTurns(0)
                .status(SpeakingSessionStatus.INIT)
                .startedAt(Instant.now())
                .build();

        SpeakingSessionEntity savedEntity = sessionJpaRepository.save(sessionEntity);
        return speakingSessionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    @Transactional
    public void saveSessionMessage(
            String sessionCode,
            int turnIndex,
            String senderType,
            MessageType messageType,
            String content,
            String contentTranslation,
            String correctedText,
            String correctionExplanation,
            String grammarNote,
            String hintForLearner,
            Double pronunciationScore) {
        saveSessionMessage(sessionCode, turnIndex, senderType, messageType, content, contentTranslation, correctedText, correctionExplanation,
                grammarNote, hintForLearner, pronunciationScore, null);
    }

    @Override
    @Transactional
    public void saveSessionMessage(
            String sessionCode,
            int turnIndex,
            String senderType,
            MessageType messageType,
            String content,
            String contentTranslation,
            String correctedText,
            String correctionExplanation,
            String grammarNote,
            String hintForLearner,
            Double pronunciationScore,
            File audioFile) {
        Optional<SpeakingSessionEntity> sessionOpt = sessionJpaRepository.findBySessionCode(sessionCode);
        if (sessionOpt.isEmpty())
            return;

        SpeakingSessionEntity session = sessionOpt.get();

        FileEntity audioFileEntity = null;
        if (audioFile != null && audioFile.getObjectKey() != null) {
            audioFileEntity = fileJpaRepository.findByObjectKey(audioFile.getObjectKey()).orElse(null);
        }

        SpeakingSessionMessageEntity messageEntity = SpeakingSessionMessageEntity.builder()
                .session(session)
                .turnIndex(turnIndex)
                .senderType(senderType)
                .messageType(messageType)
                .content(content)
                .contentTranslation(contentTranslation != null ? contentTranslation : "")
                .correctedText(correctedText)
                .correctionExplanation(correctionExplanation)
                .grammarNote(grammarNote)
                .hintForLearner(hintForLearner)
                .pronunciationScore(pronunciationScore)
                .audioFile(audioFileEntity)
                .build();

        speakingSessionMessageJpaRepository.save(messageEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isSessionCompleted(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank())
            return false;
        return sessionJpaRepository.findBySessionCode(sessionCode)
                .map(s ->
                        SpeakingSessionStatus.COMPLETED.equals(s.getStatus())
                )
                .orElse(false);
    }

    @Override
    @Transactional
    public void updateSessionTurnAndTranscript(String sessionCode, int totalTurns, String fullTranscript) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity entity = sessionJpaRepository
                .findBySessionCode(sessionCode)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        entity.setTotalTurns(totalTurns);
        entity.setFullTranscript(fullTranscript);

        sessionJpaRepository.save(entity);
    }

    @Override
    @Transactional
    public void updateSessionTurnAndTranscriptAndStatus(String sessionCode, int totalTurns, String fullTranscript, SpeakingSessionStatus status) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity entity = sessionJpaRepository
                .findBySessionCode(sessionCode)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        entity.setTotalTurns(totalTurns);
        entity.setFullTranscript(fullTranscript);
        entity.setStatus(status);

        sessionJpaRepository.save(entity);
    }

    @Override
    public int countActiveSessionsByUserId(Long userId) {
        if (userId == null) {
            return 0;
        }
        return sessionJpaRepository.countByUserIdAndStatus(userId, SpeakingSessionStatus.IN_PROGRESS);
    }

    /**
     * Xóa session bằng session code
     *
     * @param sessionCode session code, dùng để định danh session trong redis
     */
    @Override
    @Transactional
    public void deleteSessionBySessionCode(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity entity = sessionJpaRepository
                .findBySessionCode(sessionCode)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        sessionJpaRepository.delete(entity);
    }

    /**
     * Kiểm tra xem 1 session có thuộc về user không
     *
     * @param sessionCode session code để tìm
     * @param userId      chủ sở hữu
     * @return true nếu đúng là session của user
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isSessionBelongToUser(String sessionCode, Long userId) {
        if (userId == null) {
            throw new InfrastructureException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity entity = sessionJpaRepository
                .findBySessionCode(sessionCode)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        return entity.getUserId() != null && entity.getUserId().equals(userId);
    }

    @Override
    public SpeakingSession findBySessionCode(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity entity = sessionJpaRepository
                .findBySessionCode(sessionCode)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        return speakingSessionEntityMapper.entityToDomain(entity);
    }

    @Override
    public SpeakingSession findBySessionCodeAndStatus(String sessionCode, SpeakingSessionStatus status) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }
        if (status == null) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSessionEntity entity = sessionJpaRepository
                .findBySessionCodeAndStatus(sessionCode, status)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        return speakingSessionEntityMapper.entityToDomain(entity);
    }

    @Override
    public SpeakingSession findBySessionId(Long sessionId) {
        if (sessionId == null) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_SESSION_ID_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_ID_INVALID
            );
        }

        SpeakingSessionEntity entity = sessionJpaRepository
                .findById(sessionId)
                .orElseThrow(() -> new InfrastructureException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionId.toString()
                ));

        return speakingSessionEntityMapper.entityToDomain(entity);
    }

    @Override
    public List<SpeakingSession> findAllByUserIdAndSpeakingSessionStatus(
            Long userId,
            SpeakingSessionStatus status
    ) {
        return sessionJpaRepository
                .findAllByUserIdAndStatus(userId, status)
                .stream()
                .map(speakingSessionEntityMapper::entityToDomain)
                .toList();
    }
}
