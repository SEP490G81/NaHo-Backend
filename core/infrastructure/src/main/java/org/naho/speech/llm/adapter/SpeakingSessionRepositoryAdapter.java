package org.naho.speech.llm.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.naho.file.entity.FileEntity;
import org.naho.file.mapper.FileEntityMapper;
import org.naho.file.model.File;
import org.naho.file.port.out.FileStorageServicePort;
import org.naho.file.repository.FileJpaRepository;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.pagination.PageMeta;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.command.SpeakingSessionFilterCommand;
import org.naho.speech.llm.entity.SpeakingImprovedExpressionEntity;
import org.naho.speech.llm.entity.SpeakingSessionAssessmentEntity;
import org.naho.speech.llm.entity.SpeakingSessionEntity;
import org.naho.speech.llm.entity.SpeakingSessionMessageEntity;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.llm.mapper.SpeakingSessionEntityMapper;
import org.naho.speech.llm.model.SpeakingSession;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.repository.SpeakingSessionAssessmentJpaRepository;
import org.naho.speech.llm.repository.SpeakingSessionJpaRepository;
import org.naho.speech.llm.repository.SpeakingSessionMessageJpaRepository;
import org.naho.speech.llm.result.ScoringResult;
import org.naho.speech.llm.result.SpeakingSessionDetailResult;
import org.naho.speech.llm.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.specification.SpeakingSessionSpecification;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.user.exception.UserErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Component
@RequiredArgsConstructor
public class SpeakingSessionRepositoryAdapter implements SpeakingSessionRepositoryPort {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final SpeakingSessionJpaRepository sessionJpaRepository;
    private final SpeakingSessionAssessmentJpaRepository assessmentJpaRepository;
    private final SpeakingSessionMessageJpaRepository messageJpaRepository;
    private final FileJpaRepository fileJpaRepository;
    private final FileEntityMapper fileEntityMapper;
    private final FileStorageServicePort fileStorageServicePort;
    private final SpeakingSessionEntityMapper speakingSessionEntityMapper;

    @Override
    @Transactional
    public void saveSpeakingSession(
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
            ScoringResult scoringResult
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
        String strengthsJson = toJson(scoringResult.strengths());
        String weaknessesJson = toJson(scoringResult.weaknesses());
        Map<String, String> feedback = scoringResult.feedback() != null ? scoringResult.feedback() : Map.of();

        SpeakingSessionAssessmentEntity.SpeakingSessionAssessmentEntityBuilder assessmentBuilder = SpeakingSessionAssessmentEntity
                .builder()
                .session(savedSession)
                .overallScore(scoringResult.overallScore())
                .jlptEstimate(scoringResult.jlptEstimate())
                .fluencyScore(scoringResult.fluencyScore())
                .pronunciationScore(scoringResult.pronunciationScore())
                .grammarScore(scoringResult.grammarScore())
                .vocabularyScore(scoringResult.vocabularyScore())
                .interactionScore(scoringResult.interactionScore())
                .naturalnessScore(scoringResult.naturalnessScore())
                .coherenceScore(scoringResult.coherenceScore())
                .summary(scoringResult.summary())
                .strengths(strengthsJson)
                .weaknesses(weaknessesJson)
                .feedbackFluency(feedback.get("fluency"))
                .feedbackPronunciation(feedback.get("pronunciation"))
                .feedbackGrammar(feedback.get("grammar"))
                .feedbackVocabulary(feedback.get("vocabulary"))
                .feedbackInteraction(feedback.get("interaction"))
                .feedbackNaturalness(feedback.get("naturalness"))
                .feedbackCoherence(feedback.get("coherence"));

        // Thêm studyRecommendation nếu có
        if (scoringResult.studyRecommendation() != null) {
            ScoringResult.StudyRecommendation rec = scoringResult.studyRecommendation();
            assessmentBuilder
                    .studyFocusArea(rec.focusArea())
                    .studyRecommendation(rec.suggestedPractice())
                    .studyEncouragement(rec.encouragement());
        }

        SpeakingSessionAssessmentEntity savedAssessment = assessmentJpaRepository.save(assessmentBuilder.build());

        // 3. Lưu speaking_improved_expressions
        if (scoringResult.improvedExpressions() != null && !scoringResult.improvedExpressions().isEmpty()) {
            List<SpeakingImprovedExpressionEntity> expressions = new ArrayList<>();
            for (int i = 0; i < scoringResult.improvedExpressions().size(); i++) {
                ScoringResult.ImprovedExpression expr = scoringResult.improvedExpressions().get(i);
                if (expr.original() == null || expr.original().isBlank())
                    continue;
                expressions.add(SpeakingImprovedExpressionEntity.builder()
                        .assessment(savedAssessment)
                        .turnIndex(i)
                        .originalText(expr.original())
                        .improvedText(expr.improved() != null ? expr.improved() : "")
                        .explanationVi(expr.explanationVi())
                        .build());
            }
            savedAssessment.setImprovedExpressions(expressions);
            assessmentJpaRepository.save(savedAssessment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PageData<SpeakingSessionListItemResult> findUserSessions(SpeakingSessionFilterCommand command) {
        Long userId = command != null ? command.userId() : null;
        Long personaId = command != null ? command.personaId() : null;
        String search = command != null ? command.search() : null;

        int pageNumber = command != null && command.page() != null && command.page() >= 0 ? command.page() : 0;
        int pageSize = command != null && command.size() != null && command.size() > 0 ? command.size() : 10;

        Sort.Direction direction = (command != null && command.sortDirection() != null)
                ? Sort.Direction.valueOf(command.sortDirection().name())
                : Sort.Direction.DESC;
        String sortCol = (command != null && command.sortColumn() != null)
                ? command.sortColumn().getColumnName()
                : "createdTime";

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(direction, sortCol));

        String status = command != null ? command.status() : null;

        Specification<SpeakingSessionEntity> specification = Specification.allOf(
                SpeakingSessionSpecification.hasUserId(userId),
                SpeakingSessionSpecification.hasPersonaId(personaId),
                SpeakingSessionSpecification.searchByTopic(search),
                SpeakingSessionSpecification.hasStatus(status != null && !status.isBlank() ? status : "COMPLETED"));

        Page<SpeakingSessionEntity> pageResult = sessionJpaRepository.findAll(specification, pageable);

        List<SpeakingSessionListItemResult> items = pageResult.getContent().stream().map(session -> {
            int overall = 0;
            String jlpt = "N5";
            if (session.getAssessment() != null) {
                overall = session.getAssessment().getOverallScore();
                jlpt = session.getAssessment().getJlptEstimate();
            }

            int duration = session.getDurationSeconds() != null ? session.getDurationSeconds() : 0;

            return new SpeakingSessionListItemResult(
                    session.getId(),
                    session.getSessionCode(),
                    session.getTopic(),
                    session.getPersonaId(),
                    session.getMarugotoLevel(),
                    session.getFormalityLevel(),
                    overall,
                    jlpt,
                    session.getTotalTurns(),
                    duration,
                    session.getStartedAt(),
                    session.getEndedAt(),
                    session.getStatus() // thêm status để FE phân biệt
            );
        }).toList();

        return PageData.<SpeakingSessionListItemResult>builder()
                .pageMeta(PageMeta.builder()
                        .currentPage(pageResult.getNumber())
                        .pageSize(pageResult.getSize())
                        .totalPages(pageResult.getTotalPages())
                        .totalElements(pageResult.getTotalElements())
                        .hasNext(pageResult.hasNext())
                        .hasPrevious(pageResult.hasPrevious())
                        .build())
                .data(items)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SpeakingSessionDetailResult> findSessionDetailByCode(String sessionCode, Long userId) {
        return sessionJpaRepository.findBySessionCodeAndUserId(sessionCode, userId).map(session -> {
            SpeakingSessionAssessmentEntity assessment = session.getAssessment();

            int overallScore = 0;
            String jlptEstimate = "N5";
            int fluencyScore = 0;
            int pronunciationScore = 0;
            int grammarScore = 0;
            int vocabularyScore = 0;
            int interactionScore = 0;
            int naturalnessScore = 0;
            int coherenceScore = 0;
            String summary = "";
            List<String> strengths = List.of();
            List<String> weaknesses = List.of();
            Map<String, String> feedbackMap = new HashMap<>();
            List<ScoringResult.ImprovedExpression> improvedExpressions = new ArrayList<>();
            ScoringResult.StudyRecommendation studyRecommendation = null;

            if (assessment != null) {
                overallScore = assessment.getOverallScore();
                jlptEstimate = assessment.getJlptEstimate();
                fluencyScore = assessment.getFluencyScore();
                pronunciationScore = assessment.getPronunciationScore();
                grammarScore = assessment.getGrammarScore();
                vocabularyScore = assessment.getVocabularyScore();
                interactionScore = assessment.getInteractionScore();
                naturalnessScore = assessment.getNaturalnessScore();
                coherenceScore = assessment.getCoherenceScore();
                summary = assessment.getSummary();

                strengths = parseJsonList(assessment.getStrengths());
                weaknesses = parseJsonList(assessment.getWeaknesses());

                if (assessment.getFeedbackFluency() != null)
                    feedbackMap.put("fluency", assessment.getFeedbackFluency());
                if (assessment.getFeedbackPronunciation() != null)
                    feedbackMap.put("pronunciation", assessment.getFeedbackPronunciation());
                if (assessment.getFeedbackGrammar() != null)
                    feedbackMap.put("grammar", assessment.getFeedbackGrammar());
                if (assessment.getFeedbackVocabulary() != null)
                    feedbackMap.put("vocabulary", assessment.getFeedbackVocabulary());
                if (assessment.getFeedbackInteraction() != null)
                    feedbackMap.put("interaction", assessment.getFeedbackInteraction());
                if (assessment.getFeedbackNaturalness() != null)
                    feedbackMap.put("naturalness", assessment.getFeedbackNaturalness());
                if (assessment.getFeedbackCoherence() != null)
                    feedbackMap.put("coherence", assessment.getFeedbackCoherence());

                if (assessment.getImprovedExpressions() != null) {
                    for (SpeakingImprovedExpressionEntity expr : assessment.getImprovedExpressions()) {
                        improvedExpressions.add(new ScoringResult.ImprovedExpression(
                                expr.getOriginalText(),
                                expr.getImprovedText(),
                                expr.getExplanationVi()));
                    }
                }

                if (assessment.getStudyFocusArea() != null || assessment.getStudyRecommendation() != null) {
                    studyRecommendation = new ScoringResult.StudyRecommendation(
                            assessment.getStudyFocusArea(),
                            "",
                            assessment.getStudyRecommendation(),
                            assessment.getStudyEncouragement());
                }
            }

            int duration = session.getDurationSeconds() != null ? session.getDurationSeconds() : 0;

            return new SpeakingSessionDetailResult(
                    session.getId(),
                    session.getSessionCode(),
                    session.getTopic(),
                    session.getPersonaId(),
                    session.getMarugotoLevel(),
                    session.getFormalityLevel(),
                    session.getTotalTurns(),
                    duration,
                    session.getAsrConfidence(),
                    session.getFullTranscript(),
                    session.getStartedAt(),
                    session.getEndedAt(),
                    overallScore,
                    jlptEstimate,
                    fluencyScore,
                    pronunciationScore,
                    grammarScore,
                    vocabularyScore,
                    interactionScore,
                    naturalnessScore,
                    coherenceScore,
                    summary,
                    strengths,
                    weaknesses,
                    feedbackMap,
                    improvedExpressions,
                    studyRecommendation);
        });
    }

    private String toJson(Object obj) {
        if (obj == null)
            return "[]";
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> parseJsonList(String json) {
        if (json == null || json.isBlank())
            return List.of();
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    @Transactional
    public SpeakingSession createInProgressSession(
            String sessionCode,
            Long userId,
            Long personaId,
            String topic,
            String voiceName,
            MarugotoLevel marugotoLevel,
            FormalityLevel formalityLevel
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
                .status(SpeakingSessionStatus.IN_PROGRESS)
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
        messageJpaRepository.save(messageEntity);
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

//    @Override
//    @Transactional(readOnly = true)
//    public Optional<SpeakingSessionResult> findActiveSessionByCode(String sessionCode, Long userId) {
//        if (sessionCode == null || sessionCode.isBlank())
//            return Optional.empty();
//        // Khi userId = null (gọi từ ensureSessionLoadedInMemory sau pod restart),
//        // dùng findBySessionCodeAndStatus để chỉ restore đúng IN_PROGRESS, không
//        // restore COMPLETED/EXPIRED
//        Optional<SpeakingSessionEntity> sessionOpt = (userId != null)
//                ? sessionJpaRepository.findBySessionCodeAndUserId(sessionCode, userId)
//                : sessionJpaRepository.findBySessionCodeAndStatus(sessionCode, SpeakingSessionStatus.IN_PROGRESS);
//        return sessionOpt.map(this::toActiveSessionResult);
//    }

    @Override
    @Transactional
    public void updateSessionTurnAndTranscript(String sessionCode, int totalTurns, String fullTranscript) {
        sessionJpaRepository.findBySessionCode(sessionCode).ifPresent(session -> {
            session.setTotalTurns(totalTurns);
            session.setFullTranscript(fullTranscript);
            sessionJpaRepository.save(session);
        });
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

    /**
     * Kiểm tra xem session đã được start chưa (tức là có ít nhất 1 message)
     *
     * @param sessionCode session code
     * @return true nếu session đã start
     */
    @Override
    public boolean isSessionStarted(String sessionCode) {
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

        return entity.getMessages() != null && !entity.getMessages().isEmpty();
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
}
