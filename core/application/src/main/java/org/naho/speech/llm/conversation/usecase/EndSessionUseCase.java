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
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.in.EndSessionInputPort;
import org.naho.speech.llm.conversation.port.out.AiScoringPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;

import java.time.Instant;

public class EndSessionUseCase implements EndSessionInputPort {
    private final AiScoringPort aiScoringPort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final SpeakingSessionHelper speakingSessionHelper;
    private final SpeakingSessionResultMapper speakingSessionResultMapper;
    private final CrudUserDailyMissionInputPort crudUserDailyMissionInputPort;
    private final UserLearningStreakInputPort userLearningStreakInputPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final TransactionPort transactionPort;

    public EndSessionUseCase(
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            PersonaRepositoryPort personaRepositoryPort,
            SpeakingSessionHelper speakingSessionHelper,
            SpeakingSessionResultMapper speakingSessionResultMapper,
            CrudUserDailyMissionInputPort crudUserDailyMissionInputPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            TransactionPort transactionPort
    ) {
        this.aiScoringPort = aiScoringPort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.personaRepositoryPort = personaRepositoryPort;
        this.speakingSessionHelper = speakingSessionHelper;
        this.speakingSessionResultMapper = speakingSessionResultMapper;
        this.crudUserDailyMissionInputPort = crudUserDailyMissionInputPort;
        this.userLearningStreakInputPort = userLearningStreakInputPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.transactionPort = transactionPort;
    }

    @Override
    public SpeakingSessionResult endSession(Long requestUserId, String sessionCode, String topic, String speechMetaData, String arsConfidence) {
        return transactionPort.execute(() -> doEndSession(requestUserId, sessionCode, topic, speechMetaData, arsConfidence));
    }

    private SpeakingSessionResult doEndSession(Long requestUserId, String sessionCode, String topic, String speechMetaData, String arsConfidence) {
        SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);

        Long userId = speakingSession.getUserId() != null ? speakingSession.getUserId() : requestUserId;
        if (userId == null) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_NOT_FOUND,
                    LlmDetailMessageKey.LLM_SESSION_NOT_FOUND
            );
        }

        Persona persona = personaRepositoryPort.findById(speakingSession.getPersonaId()).orElse(null);
        String personaContext = persona != null
                ? speakingSessionHelper.buildPersonaContext(persona, speakingSession.getFormalityLevel(), speakingSession.getMarugotoLevel())
                : "";

        String fullTranscript = speakingSession.getFullTranscript() != null ? speakingSession.getFullTranscript() : "";
        String effectiveTopic = (topic != null && !topic.isBlank())
                ? topic
                : (speakingSession.getTopic() != null ? speakingSession.getTopic() : "");
        
        // LLM chấm điểm
        SpeakingSessionAssessmentResult speakingSessionAssessmentResult =
                aiScoringPort.score(
                        sessionCode,
                        effectiveTopic,
                        fullTranscript,
                        speechMetaData,
                        arsConfidence,
                        personaContext
                );

        // Persist session result to DB
        SpeakingSession savedSession;
        try {
            Long personaId = speakingSession.getPersonaId();
            MarugotoLevel marugotoLevel = speakingSession.getMarugotoLevel();
            FormalityLevel formalityLevel = speakingSession.getFormalityLevel();
            int totalTurns = speakingSession.getTotalTurns();
            Instant startedAt = speakingSession.getStartedAt();

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

            savedSession = speakingSessionRepositoryPort.saveSpeakingSession(
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
                    speakingSessionAssessmentResult
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

            return speakingSessionResultMapper.domainToResult(savedSession);

        } catch (Exception e) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SAVE_SESSION_FAILED,
                    LlmDetailMessageKey.LLM_SAVE_SESSION_FAILED
            );
        }
    }
}

