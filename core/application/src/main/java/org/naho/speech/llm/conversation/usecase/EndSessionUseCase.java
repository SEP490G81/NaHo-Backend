package org.naho.speech.llm.conversation.usecase;

import org.naho.daily.command.CompleteDailyMissionCommand;
import org.naho.daily.port.in.CrudUserDailyMissionInputPort;
import org.naho.daily.type.MissionType;
import org.naho.i18n.message.learning.UserLearningProgressDetailMessageKey;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.learning.command.UpdateUserStreakCommand;
import org.naho.learning.exception.UserLearningProgressErrorCode;
import org.naho.learning.model.UserLearningProgress;
import org.naho.learning.port.in.UserLearningStreakInputPort;
import org.naho.learning.port.out.UserLearningProgressRepositoryPort;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.shared.constant.SystemZoneId;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.conversation.constant.AiMessageField;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.helper.SpeakingSessionHelper;
import org.naho.speech.llm.conversation.mapper.SpeakingSessionAssessmentResultMapper;
import org.naho.speech.llm.conversation.port.in.EndSessionInputPort;
import org.naho.speech.llm.conversation.port.out.*;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.SpeakingSessionStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.naho.speech.llm.conversation.helper.SpeakingSessionHelper.MAX_SLIDING_WINDOW_MESSAGES;

public class EndSessionUseCase implements EndSessionInputPort {
    private final AiScoringPort aiScoringPort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final SpeakingSessionHelper speakingSessionHelper;
    private final SpeakingSessionAssessmentResultMapper speakingSessionAssessmentResultMapper;
    private final TransactionPort transactionPort;
    private final SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort;
    private final AiChatPort aiChatPort;
    private final SpeakingSessionAssessmentRepositoryPort speakingSessionAssessmentRepositoryPort;
    private final CrudUserDailyMissionInputPort crudUserDailyMissionInputPort;
    private final UserLearningProgressRepositoryPort userLearningProgressRepositoryPort;
    private final UserLearningStreakInputPort userLearningStreakInputPort;

    public EndSessionUseCase(
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            PersonaRepositoryPort personaRepositoryPort,
            SpeakingSessionHelper speakingSessionHelper,
            SpeakingSessionAssessmentResultMapper speakingSessionAssessmentResultMapper,
            TransactionPort transactionPort,
            SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort,
            AiChatPort aiChatPort,
            SpeakingSessionAssessmentRepositoryPort speakingSessionAssessmentRepositoryPort,
            CrudUserDailyMissionInputPort crudUserDailyMissionInputPort,
            UserLearningProgressRepositoryPort userLearningProgressRepositoryPort,
            UserLearningStreakInputPort userLearningStreakInputPort
    ) {
        this.aiScoringPort = aiScoringPort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.personaRepositoryPort = personaRepositoryPort;
        this.speakingSessionHelper = speakingSessionHelper;
        this.speakingSessionAssessmentResultMapper = speakingSessionAssessmentResultMapper;
        this.transactionPort = transactionPort;
        this.speakingSessionMessageRepositoryPort = speakingSessionMessageRepositoryPort;
        this.aiChatPort = aiChatPort;
        this.speakingSessionAssessmentRepositoryPort = speakingSessionAssessmentRepositoryPort;
        this.crudUserDailyMissionInputPort = crudUserDailyMissionInputPort;
        this.userLearningProgressRepositoryPort = userLearningProgressRepositoryPort;
        this.userLearningStreakInputPort = userLearningStreakInputPort;
    }

    @Override
    public SpeakingSessionAssessmentResult endSession(Long userId, String sessionCode) {
        return transactionPort.execute(() -> doEndSession(userId, sessionCode));
    }

    private SpeakingSessionAssessmentResult doEndSession(Long userId, String sessionCode) {
        SpeakingSession speakingSession = speakingSessionRepositoryPort
                .findBySessionCode(sessionCode);

        // nếu session không thuộc về user
        if (!Objects.equals(speakingSession.getUserId(), userId)) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_NOT_FOUND,
                    LlmDetailMessageKey.LLM_SESSION_NOT_FOUND
            );
        }

        // nếu trạng thái không phải IN_PROGRESS thì không cho nó end
        if (!SpeakingSessionStatus.IN_PROGRESS.equals(speakingSession.getStatus())) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_ALREADY_COMPLETED,
                    LlmDetailMessageKey.LLM_SESSION_ALREADY_COMPLETED
            );
        }

        // lấy ra các message trong lịch sử chat của session
        List<SpeakingSessionMessage> previousMessages = speakingSessionMessageRepositoryPort
                .findAllBySessionId(speakingSession.getId());

        // Nếu người dùng chưa chat tí nào mà đã end
        // <= 1 bởi vì AI luôn là người chào đầu tiên
        if (previousMessages.size() <= 1) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_TRANSCRIPT_BLANK,
                    LlmDetailMessageKey.LLM_TRANSCRIPT_BLANK
            );
        }

        Persona persona = personaRepositoryPort
                .findById(speakingSession.getPersonaId())
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        speakingSession.getPersonaId()
                ));

        // Build system prompt (đã bao gồm prompt của persona)
        String systemPromptContent = speakingSessionHelper.buildSystemPromptContent(
                persona,
                speakingSession.getFormalityLevel(),
                speakingSession.getMarugotoLevel()
        );

        // cắt bớt lịch sử chat
        if (previousMessages.size() > MAX_SLIDING_WINDOW_MESSAGES) {
            previousMessages = previousMessages.subList(previousMessages.size() - MAX_SLIDING_WINDOW_MESSAGES, previousMessages.size());
        }

        List<Map<String, String>> sessionHistories = new ArrayList<>();

        // Chuyển lịch sử chat thành dạng List<Map<String, String>>
        for (SpeakingSessionMessage speakingSessionMessage : previousMessages) {
            sessionHistories.add(Map.of(
                    AiMessageField.ROLE, speakingSessionMessage.getSenderType().name().toLowerCase(),
                    AiMessageField.CONTENT, speakingSessionMessage.getContent()
            ));
        }

        // Chuyển lịch sử chat thành JSON dạng: [{"role": "", "content": ""},...]
        String messagesJson = aiChatPort.buildMessagesRequestBody(sessionHistories);

        // LLM chấm điểm
        SpeakingSessionAssessment speakingSessionAssessment =
                aiScoringPort.score(
                        sessionCode,
                        "Conversation with " + persona.getName(),
                        systemPromptContent,
                        messagesJson
                );

        // Lưu chấm điểm (speaking session assessment)
        speakingSessionAssessment.setSpeakingSessionId(speakingSession.getId());

        SpeakingSessionAssessment savedSpeakingSessionAssessment =
                speakingSessionAssessmentRepositoryPort.save(speakingSessionAssessment);

        // Lưu speaking session
        speakingSession.setEndedAt(Instant.now());
        speakingSession.setStatus(SpeakingSessionStatus.COMPLETED);
        speakingSessionRepositoryPort.save(speakingSession);

        // hoàn thành nhiệm vụ hàng ngày (trò chuyện 1:1 với AI)
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

        // Cập nhật chuỗi học hàng ngày của người dùng
        progress = userLearningStreakInputPort.updateUserLearningStreak(
                UpdateUserStreakCommand.builder()
                        .userLearningProgress(progress)
                        .userId(userId)
                        .now(Instant.now())
                        .zoneId(SystemZoneId.HO_CHI_MINH_ZONE_ID)
                        .build()
        );

        // Lưu tiến trình học của người dùng
        userLearningProgressRepositoryPort.save(progress);

        return speakingSessionAssessmentResultMapper.domainToResult(savedSpeakingSessionAssessment);
    }
}

