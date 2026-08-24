package org.naho.speech.llm.conversation.usecase;

import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
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

    public EndSessionUseCase(
            AiScoringPort aiScoringPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            PersonaRepositoryPort personaRepositoryPort,
            SpeakingSessionHelper speakingSessionHelper,
            SpeakingSessionAssessmentResultMapper speakingSessionAssessmentResultMapper,
            TransactionPort transactionPort,
            SpeakingSessionMessageRepositoryPort speakingSessionMessageRepositoryPort,
            AiChatPort aiChatPort,
            SpeakingSessionAssessmentRepositoryPort speakingSessionAssessmentRepositoryPort
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

        // lấy ra các message trong lịch sử chat của session
        List<SpeakingSessionMessage> previousMessages = speakingSessionMessageRepositoryPort
                .findAllBySessionId(speakingSession.getId());

        // cắt bớt lịch sử chat
        if (previousMessages.size() > MAX_SLIDING_WINDOW_MESSAGES) {
            previousMessages = previousMessages.subList(previousMessages.size() - MAX_SLIDING_WINDOW_MESSAGES, previousMessages.size());
        }

        List<Map<String, String>> sessionHistories = new ArrayList<>();

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

        return speakingSessionAssessmentResultMapper.domainToResult(savedSpeakingSessionAssessment);
    }
}

