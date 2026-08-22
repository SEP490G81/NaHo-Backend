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
import org.naho.speech.llm.conversation.constant.AiMessageField;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.conversation.port.in.EndSessionInputPort;
import org.naho.speech.llm.conversation.port.out.AiChatPort;
import org.naho.speech.llm.conversation.port.out.AiScoringPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionMessageRepositoryPort;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.naho.speech.llm.conversation.helper.SpeakingSessionHelper.MAX_SLIDING_WINDOW_MESSAGES;

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
    private final SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort;
    private final AiChatPort aiChatPort;

    public EndSessionUseCase(
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            PersonaRepositoryPort personaRepositoryPort,
            SpeakingSessionHelper speakingSessionHelper,
            SpeakingSessionResultMapper speakingSessionResultMapper,
            CrudUserDailyMissionInputPort crudUserDailyMissionInputPort,
            UserLearningStreakInputPort userLearningStreakInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            TransactionPort transactionPort,
            SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort,
            AiChatPort aiChatPort
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
        this.speakingSessionMessageRepositoryPort = speakingSessionMessageRepositoryPort;
        this.aiChatPort = aiChatPort;
    }

    @Override
    public SpeakingSessionResult endSession(Long userId, String sessionCode) {
        return null;
    }

    private SpeakingSessionResult doEndSession(Long userId, String sessionCode) {
        SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);

        Persona persona = personaRepositoryPort
                .findById(speakingSession.getPersonaId())
                .orElse(() -> new ApplicationException());

        String systemPromptContent = speakingSessionHelper.buildSystemPromptContent(
                persona,
                speakingSession.getFormalityLevel(),
                speakingSession.getMarugotoLevel()
        );

        List<SpeakingSessionMessage> previousMessages = speakingSessionMessageRepositoryPort
                .findAllBySessionId(speakingSession.getId());

        List<Map<String, String>> sessionHistories = new ArrayList<>();
        if (previousMessages.size() > MAX_SLIDING_WINDOW_MESSAGES) {
            previousMessages = previousMessages.subList(previousMessages.size() - MAX_SLIDING_WINDOW_MESSAGES, previousMessages.size());
        }

        for (SpeakingSessionMessage speakingSessionMessage : previousMessages) {
            sessionHistories.add(Map.of(
                    AiMessageField.ROLE, speakingSessionMessage.getSenderType().name().toLowerCase(),
                    AiMessageField.CONTENT, speakingSessionMessage.getContent()
            ));
        }

        String messagesJson = aiChatPort.buildMessagesRequestBody(sessionHistories);

        // LLM chấm điểm
        SpeakingSessionAssessmentResult speakingSessionAssessmentResult =
                aiScoringPort.score(
                        sessionCode,
                        "Conversation with " + persona.getName(),
                        systemPromptContent,
                        messagesJson
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

//            savedSession = speakingSessionRepositoryPort.saveSpeakingSession(
//                    sessionCode,
//                    userId,
//                    personaId,
//                    effectiveTopic,
//                    marugotoLevel,
//                    formalityLevel,
//                    fullTranscript,
//                    totalTurns,
//                    asrConfidenceDouble,
//                    startedAt,
//                    speakingSessionAssessmentResult
//            );

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

//            return speakingSessionResultMapper.domainToResult(savedSession);
            return null;
        } catch (Exception e) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SAVE_SESSION_FAILED,
                    LlmDetailMessageKey.LLM_SAVE_SESSION_FAILED
            );
        }
    }
}

