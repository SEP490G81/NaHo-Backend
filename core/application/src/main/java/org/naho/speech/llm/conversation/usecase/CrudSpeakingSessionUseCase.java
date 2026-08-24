package org.naho.speech.llm.conversation.usecase;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionAssessmentResultMapper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.in.CrudSpeakingSessionInputPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionAssessmentRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.user.exception.UserErrorCode;

import java.util.List;

public class CrudSpeakingSessionUseCase implements CrudSpeakingSessionInputPort {
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final SpeakingSessionResultMapper speakingSessionResultMapper;
    private final SpeakingSessionAssessmentRepositoryPort speakingSessionAssessmentRepositoryPort;
    private final SpeakingSessionAssessmentResultMapper speakingSessionAssessmentResultMapper;

    public CrudSpeakingSessionUseCase(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            SpeakingSessionResultMapper speakingSessionResultMapper,
            SpeakingSessionAssessmentRepositoryPort speakingSessionAssessmentRepositoryPort,
            SpeakingSessionAssessmentResultMapper speakingSessionAssessmentResultMapper
    ) {
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.speakingSessionResultMapper = speakingSessionResultMapper;
        this.speakingSessionAssessmentRepositoryPort = speakingSessionAssessmentRepositoryPort;
        this.speakingSessionAssessmentResultMapper = speakingSessionAssessmentResultMapper;
    }

    @Override
    public List<SpeakingSessionListItemResult> findAllByUserIdAndSpeakingSessionStatus(
            Long userId,
            SpeakingSessionStatus status
    ) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (status == null) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_STATUS_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_STATUS_INVALID
            );
        }

        List<SpeakingSession> inProgressSessions = speakingSessionRepositoryPort
                .findAllByUserIdAndSpeakingSessionStatus(userId, status);

        return inProgressSessions.stream()
                .map(speakingSessionResultMapper::domainToListItemResult)
                .toList();
    }

    @Override
    public SpeakingSessionResult findByUserIdAndSpeakingSessionCodeAndSpeakingSessionStatus(Long userId, String sessionCode, SpeakingSessionStatus status) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (status == null) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_STATUS_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_STATUS_INVALID
            );
        }

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        SpeakingSession speakingSession = speakingSessionRepositoryPort
                .findByUserIdAndSpeakingSessionCodeAndSpeakingSessionStatus(userId, sessionCode, status);

        return speakingSessionResultMapper.domainToResult(speakingSession);
    }

    @Override
    public SpeakingSessionResult findBySessionCodeAndUserId(String sessionCode, Long userId) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        SpeakingSession speakingSession = speakingSessionRepositoryPort
                .findByUserIdAndSpeakingSessionCodeAndSpeakingSessionStatus(
                        userId,
                        sessionCode,
                        SpeakingSessionStatus.COMPLETED
                );

        SpeakingSessionAssessment speakingSessionAssessment = speakingSessionAssessmentRepositoryPort
                .findBySpeakingSession_Id(speakingSession.getId())
                .orElseThrow(() -> new ApplicationException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND,
                        sessionCode
                ));

        SpeakingSessionAssessmentResult speakingSessionAssessmentResult = speakingSessionAssessmentResultMapper.domainToResult(speakingSessionAssessment);

        return speakingSessionResultMapper.domainToResult(speakingSession, speakingSessionAssessmentResult);
    }

    /**
     * Xóa session bằng session code và user id
     *
     * @param sessionCode mã phiên trò chuyện
     * @param userId      người sở hữu phiên trò chuyện
     */
    @Override
    public void deleteSessionBySessionCodeAndUserId(String sessionCode, Long userId) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        speakingSessionRepositoryPort.deleteSessionBySessionCodeAndUserId(sessionCode, userId);
    }
}
