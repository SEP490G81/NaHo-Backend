package org.naho.speech.llm.conversation.usecase;

import org.naho.daily.command.CompleteDailyMissionCommand;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.daily.type.MissionType;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.learning.command.UpdateUserStreakCommand;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.port.in.EndSessionInputPort;
import org.naho.speech.llm.conversation.port.out.AiScoringPort;
import org.naho.speech.llm.conversation.port.out.SessionStorePort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.result.ScoringResult;

import java.time.Instant;

public class EndSessionUseCase implements EndSessionInputPort {
    private final SessionStorePort sessionStorePort;
    private final AiScoringPort aiScoringPort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final CrudUserDailyMissionInputPort crudUserDailyMissionInputPort;
    private final UserLearningStreakInputPort userLearningStreakInputPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final TransactionPort transactionPort;

    public EndSessionUseCase(
            SessionStorePort sessionStorePort,
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            CrudUserDailyMissionInputPort crudUserDailyMissionInputPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            TransactionPort transactionPort
    ) {
        this.sessionStorePort = sessionStorePort;
        this.aiScoringPort = aiScoringPort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.crudUserDailyMissionInputPort = crudUserDailyMissionInputPort;
        this.userLearningStreakInputPort = userLearningStreakInputPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public ScoringResult endSession(Long requestUserId, String sessionCode, String topic, String speechMetaData, String arsConfidence) {
        return transactionPort.execute(() -> doEndSession(requestUserId, sessionCode, topic, speechMetaData, arsConfidence));
    }

    private ScoringResult doEndSession(Long requestUserId, String sessionCode, String topic, String speechMetaData, String arsConfidence) {
        // Lấy user id của người sở hữu cái session này
        Long userId = sessionStorePort.getUserId(sessionCode);
        if (userId == null) {
            userId = requestUserId;
        }

        if (userId == null) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_NOT_FOUND,
                    LlmDetailMessageKey.LLM_SESSION_NOT_FOUND
            );
        }

        String fullTranscript = sessionStorePort.getFullTranscript(sessionCode);
        String personaContext = sessionStorePort.getPersonaContext(sessionCode);

        String effectiveTopic = (topic != null && !topic.isBlank()) ? topic : sessionStorePort.getTopic(sessionCode);

        System.out.println("    Transcript length: " + fullTranscript.length() + " chars");
        System.out.println("    Persona context: " + (personaContext.isBlank() ? "(none)" : personaContext.substring(0, Math.min(80, personaContext.length()))));

        ScoringResult result = aiScoringPort.score(sessionCode, effectiveTopic, fullTranscript, speechMetaData, arsConfidence, personaContext);
        System.out.println("    overallScore: " + result.overallScore() + "/100");
        System.out.println("    jlptEstimate: " + result.jlptEstimate());

        // Persist session result to DB
        try {
            Long personaId = sessionStorePort.getPersonaId(sessionCode);
            MarugotoLevel marugotoLevel = sessionStorePort.getMarugotoLevel(sessionCode);
            FormalityLevel formalityLevel = sessionStorePort.getFormalityLevel(sessionCode);
            int totalTurns = sessionStorePort.getTurnCount(sessionCode);
            Instant startedAt = sessionStorePort.getStartedAt(sessionCode);

            Double asrConfidenceDouble = null;
            if (arsConfidence != null && !arsConfidence.isBlank() && !arsConfidence.equals("N/A")) {
                try {
                    asrConfidenceDouble = Double.parseDouble(arsConfidence.trim());
                } catch (NumberFormatException ignored) {
                    throw new ApplicationException(
                            LlmApplicationError.LLM_PARSE_ERROR,
                            LlmDetailMessageKey.LLM_PARSE_ERROR
                    );
                }
            }

            speakingSessionRepositoryPort.saveSpeakingSession(
                    sessionCode,
                    userId,
                    personaId,
                    effectiveTopic,
                    marugotoLevel,
                    formalityLevel,
                    fullTranscript,
                    totalTurns,
                    asrConfidenceDouble,
                    startedAt,
                    result
            );

            crudUserDailyMissionInputPort.completeMission(
                    new CompleteDailyMissionCommand(
                            userId,
                            MissionType.TALK_WITH_AI
                    )
            );

            // Lấy thông tin về thành tích học tập của người dùng
            UserLearningProgress progress = userLearningProgressRepositoryPort
                    .findByUserId(userId)
                    .orElseThrow(() -> new ApplicationException(
                            UserLearningProgressErrorCode.USER_LEARNING_PROGRESS_NOT_FOUND,
                            UserLearningProgressDetailMessageKey.USER_LEARNING_PROGRESS_NOT_FOUND_BY_USER_ID
                    ));

            progress = userLearningStreakInputPort.updateUserLearningStreak(
                    UpdateUserStreakCommand.builder()
                            .userLearningProgress(progress)
                            .userId(userId)
                            .now(Instant.now())
                            .zoneId(SystemZoneId.HO_CHI_MINH_ZONE_ID)
                            .build()
            );

            userLearningProgressRepositoryPort.save(progress);

            System.out.println("[EndSessionUseCase] Successfully saved session " + sessionCode + " to DB for userId: " + userId);
        } catch (Exception e) {
            System.err.println("[EndSessionUseCase] Failed to persist session to DB: " + e.getMessage());
            throw new ApplicationException(
                    LlmApplicationError.LLM_SAVE_SESSION_FAILED,
                    LlmDetailMessageKey.LLM_SAVE_SESSION_FAILED
            );
        }

        sessionStorePort.clearSession(sessionCode);
        return result;
    }
}

