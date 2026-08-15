package org.naho.speech.llm.usecase;

import org.naho.file.constant.FileAccessStatus;
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.SpeakingSessionFilterCommand;
import org.naho.speech.llm.command.StartSpeakingConversationWithAICommand;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.llm.helper.SpeakingSessionHelper;
import org.naho.speech.llm.helper.SpeakingSessionHelper.ParsedAiReply;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.out.AiChatPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.port.out.SpeechToTextPort;
import org.naho.speech.llm.result.ActiveSpeakingSessionResult;
import org.naho.speech.llm.result.AudioChatResult;
import org.naho.speech.llm.result.ChatResult;
import org.naho.speech.llm.result.SpeakingSessionDetailResult;
import org.naho.speech.llm.result.SpeakingSessionListItemResult;
import org.naho.speech.llm.result.SpeechToTextResult;
import org.naho.speech.llm.result.StartConversationResult;
import org.naho.speech.llm.validator.SessionValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SpeakingSessionUseCase implements SpeakingSessionInputPort {

    private final AiChatPort aiChatPort;
    private final SessionStorePort sessionStorePort;
    private final SpeechToTextPort speechToTextPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final TextToSpeechServicePort textToSpeechServicePort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final SessionValidator sessionValidator;
    private final SpeakingSessionHelper speakingSessionHelper;

    public SpeakingSessionUseCase(
            AiChatPort aiChatPort,
            SessionStorePort sessionStorePort,
            SpeechToTextPort speechToTextPort,
            PersonaRepositoryPort personaRepositoryPort,
            TextToSpeechServicePort textToSpeechServicePort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            SessionValidator sessionValidator,
            SpeakingSessionHelper speakingSessionHelper
    ) {
        this.aiChatPort = aiChatPort;
        this.sessionStorePort = sessionStorePort;
        this.speechToTextPort = speechToTextPort;
        this.personaRepositoryPort = personaRepositoryPort;
        this.textToSpeechServicePort = textToSpeechServicePort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.sessionValidator = sessionValidator;
        this.speakingSessionHelper = speakingSessionHelper;
    }

    public SpeakingSessionUseCase(
            AiChatPort aiChatPort,
            SessionStorePort sessionStorePort,
            SpeechToTextPort speechToTextPort,
            PersonaRepositoryPort personaRepositoryPort,
            TextToSpeechServicePort textToSpeechServicePort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            SessionValidator sessionValidator
    ) {
        this(
                aiChatPort,
                sessionStorePort,
                speechToTextPort,
                personaRepositoryPort,
                textToSpeechServicePort,
                speakingSessionRepositoryPort,
                fileRepositoryPort,
                uploadFileInputPort,
                sessionValidator,
                new SpeakingSessionHelper(sessionStorePort, speakingSessionRepositoryPort, textToSpeechServicePort)
        );
    }

    @Override
    public ChatResult sendMessage(SendMessageWithSessionCommand command) {
        String sessionId = command.sessionId();
        sessionValidator.validateSessionNotCompleted(sessionId);
        sessionValidator.validateSessionTurnLimit(sessionId, null);
        speakingSessionHelper.ensureSessionLoadedInMemory(sessionId);
        String userMessage = command.userMessage();
        sessionStorePort.addMessage(sessionId, "user", userMessage);

        List<Map<String, String>> messages = speakingSessionHelper.getSlidingWindowMessages(sessionId);
        String rawReply = aiChatPort.chatWithContext(messages);
        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionId, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + userMessage + "\nAssistant: " + parsed.reply() + "\n");
        int currentTurn = sessionStorePort.incrementTurnCount(sessionId);

        // Async persistence to DB via Virtual Threads to optimize turn latency
        final String sCode = sessionId;
        final int sTurn = currentTurn;
        final String uMsg = userMessage;
        final String aMsg = parsed.reply();
        final String cText = parsed.correctedUserText();
        final String cExp = parsed.correctionExplanation();
        final String gNote = parsed.grammarNote();
        final String hLearner = parsed.hintForLearner();
        final String fTranscript = sessionStorePort.getFullTranscript(sessionId);

        Thread.ofVirtual().start(() -> {
            try {
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "user", uMsg,
                        cText, cExp, null, hLearner, null);
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "assistant", aMsg, null,
                        null, gNote, null, null);
                speakingSessionRepositoryPort.updateSessionTurnAndTranscript(sCode, sTurn, fTranscript);
            } catch (Exception e) {
                System.err.println(
                        "[SpeakingSessionUseCase] Failed to persist turn message asynchronously: " + e.getMessage());
            }
        });

        String aiAudio = speakingSessionHelper.toAudioBase64(sessionId, parsed.reply());

        return new ChatResult(
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote(),
                parsed.correctedUserText(),
                parsed.correctionExplanation(),
                aiAudio);
    }

    @Override
    public void sendMessageStream(SendMessageWithSessionCommand command, Consumer<String> onToken) {
        String sessionId = command.sessionId();
        sessionValidator.validateSessionNotCompleted(sessionId);
        sessionValidator.validateSessionTurnLimit(sessionId, null);
        speakingSessionHelper.ensureSessionLoadedInMemory(sessionId);
        String userMessage = command.userMessage();
        sessionStorePort.addMessage(sessionId, "user", userMessage);

        List<Map<String, String>> messages = speakingSessionHelper.getSlidingWindowMessages(sessionId);
        StringBuilder fullReply = new StringBuilder();
        aiChatPort.chatStreamWithContext(messages, token -> {
            fullReply.append(token);
            onToken.accept(token);
        });

        String rawReply = fullReply.toString();
        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionId, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + userMessage + "\nAssistant: " + parsed.reply() + "\n");
        sessionStorePort.incrementTurnCount(sessionId);
    }

    @Override
    public StartConversationResult startConversationWithAISession(
            StartSpeakingConversationWithAICommand command
    ) {
        // Kiểm tra xem người dùng đã tới giới hạn lượt tạo session trong ngày chưa
        sessionValidator.validateSessionStartLimit(command.userId());

        String sessionId = UUID.randomUUID().toString();
        Persona persona = personaRepositoryPort.findById((long) command.personaId())
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        command.personaId()));

        sessionStorePort.initSession(sessionId);
        sessionStorePort.setTopic(sessionId, "Conversation with " + persona.getName());
        sessionStorePort.setVoiceName(sessionId, "ja-JP-NanamiNeural");

        FormalityLevel effectiveFormality = command.formalityLevelOverride();
        if (effectiveFormality == null && persona.getConversationStyle() != null) {
            effectiveFormality = persona.getConversationStyle().getFormalityLevel();
        }

        MarugotoLevel effectiveMarugoto = command.marugotoLevelOverride();
        if (effectiveMarugoto == null && persona.getConversationStyle() != null) {
            effectiveMarugoto = persona.getConversationStyle().getMarugotoLevel();
        }

        StringBuilder personaCtx = new StringBuilder();
        personaCtx.append("Persona name: ").append(persona.getName()).append("\n");
        if (persona.getPrompt() != null) {
            personaCtx.append("Persona role: ").append(persona.getPrompt()).append("\n");
        }
        if (persona.getConversationStyle() != null) {
            if (persona.getConversationStyle().getDescription() != null) {
                personaCtx.append("Style description: ").append(persona.getConversationStyle().getDescription())
                        .append("\n");
            }
            if (persona.getConversationStyle().getPrompt() != null) {
                personaCtx.append("Style instructions: ").append(persona.getConversationStyle().getPrompt())
                        .append("\n");
            }
        }
        if (effectiveFormality != null) {
            personaCtx.append("formalityLevel: ").append(effectiveFormality.name()).append("\n");
        }
        if (effectiveMarugoto != null) {
            personaCtx.append("marugotoLevel: ").append(effectiveMarugoto.name()).append("\n");
        }
        sessionStorePort.setPersonaContext(sessionId, personaCtx.toString());

        StringBuilder customInstruction = new StringBuilder(SpeakingSessionHelper.PERSONA_INSTRUCTION);
        customInstruction.append("\n- Your persona role & prompt: ").append(persona.getPrompt());
        if (persona.getConversationStyle() != null) {
            if (persona.getConversationStyle().getDescription() != null) {
                customInstruction.append("\n- Conversation style description: ")
                        .append(persona.getConversationStyle().getDescription());
            }
            if (persona.getConversationStyle().getPrompt() != null) {
                customInstruction.append("\n- Conversation style prompt: ")
                        .append(persona.getConversationStyle().getPrompt());
            }
        }
        if (effectiveFormality != null) {
            customInstruction.append("\n- Formality level (Keigo/Style): ").append(effectiveFormality.name());
        }
        if (effectiveMarugoto != null) {
            customInstruction.append("\n- Marugoto course level: ").append(effectiveMarugoto.name());
        }

        // Store session metadata for DB persistence
        sessionStorePort.setUserId(sessionId, command.userId());
        sessionStorePort.setPersonaId(sessionId, (long) command.personaId());
        if (effectiveMarugoto != null) {
            sessionStorePort.setMarugotoLevel(sessionId, effectiveMarugoto.name());
        }
        if (effectiveFormality != null) {
            sessionStorePort.setFormalityLevel(sessionId, effectiveFormality.name());
        }

        String prompt = SpeakingSessionHelper.SYSTEM_PROMPT_TEMPLATE.formatted(customInstruction.toString());
        sessionStorePort.addMessage(sessionId, "system", prompt);
        sessionStorePort.addMessage(sessionId, "user", "こんにちは、話しましょう！");

        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionId);
        String rawReply = aiChatPort.chatWithContext(messages);
        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionId, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: こんにちは、話しましょう！\nAssistant: " + parsed.reply() + "\n");

        // Create IN_PROGRESS session in DB
        try {
            speakingSessionRepositoryPort.createInProgressSession(
                    sessionId,
                    command.userId(),
                    (long) command.personaId(),
                    "Conversation with " + persona.getName(),
                    effectiveMarugoto != null ? effectiveMarugoto.name() : null,
                    effectiveFormality != null ? effectiveFormality.name() : null);
            speakingSessionRepositoryPort.saveSessionMessage(sessionId, 0, "assistant", parsed.reply(), null, null,
                    parsed.grammarNote(), null, null);
        } catch (Exception e) {
            System.err
                    .println("[SpeakingSessionUseCase] Failed to create IN_PROGRESS session in DB: " + e.getMessage());
        }

        String audioBase64 = speakingSessionHelper.toAudioBase64(sessionId, parsed.reply());

        return new StartConversationResult(
                sessionId,
                audioBase64,
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote());
    }

    @Override
    public AudioChatResult sendAudioMessage(SendAudioMessageCommand command) {
        String sessionId = command.sessionId();
        sessionValidator.validateSessionNotCompleted(sessionId);
        sessionValidator.validateSessionTurnLimit(sessionId, command.userId());
        speakingSessionHelper.ensureSessionLoadedInMemory(sessionId);

        SpeechToTextResult sttResult = speechToTextPort.transcribeAndAssess(
                command.audioBytes(), command.referenceText());

        String transcribedText = sttResult.transcribedText();
        System.out.println("[SpeakingSession] STT result: " + transcribedText);

        sessionStorePort.addMessage(sessionId, "user", transcribedText);

        List<Map<String, String>> messages = speakingSessionHelper.getSlidingWindowMessages(sessionId);
        String rawReply = aiChatPort.chatWithContext(messages);

        for (Map<String, String> m : messages) {
            System.out.println("[SpeakingSession] Message: " + m.get("content"));
        }

        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionId, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + transcribedText + "\nAssistant: " + parsed.reply() + "\n");
        int currentTurn = sessionStorePort.incrementTurnCount(sessionId);

        // Async persistence to DB via Virtual Threads to optimize turn latency
        final String sCode = sessionId;
        final int sTurn = currentTurn;
        final String uMsg = transcribedText;
        final String aMsg = parsed.reply();
        final String cText = parsed.correctedUserText();
        final String cExp = parsed.correctionExplanation();
        final String gNote = parsed.grammarNote();
        final String hLearner = parsed.hintForLearner();
        final Double pronScore = sttResult.pronunciationScore();
        final String fTranscript = sessionStorePort.getFullTranscript(sessionId);

        final StoredFile storedFile = command.storedFile();

        Thread.ofVirtual().start(() -> {
            try {
                File audioFile = null;
                if (storedFile != null) {
                    audioFile = fileRepositoryPort.createNewForUpload(storedFile, FileAccessStatus.PRIVATE);
                }
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "user", uMsg,
                        cText, cExp, null, hLearner, pronScore, audioFile);
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "assistant", aMsg, null,
                        null, gNote, null, null);
                speakingSessionRepositoryPort.updateSessionTurnAndTranscript(sCode, sTurn, fTranscript);

                if (storedFile != null) {
                    uploadFileInputPort.uploadFileToCloud(storedFile);
                }
            } catch (Exception e) {
                System.err.println(
                        "[SpeakingSessionUseCase] Failed to persist audio message asynchronously: " + e.getMessage());
            }
        });

        String aiAudio = speakingSessionHelper.toAudioBase64(sessionId, parsed.reply());

        return new AudioChatResult(
                transcribedText,
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote(),
                parsed.correctedUserText(),
                parsed.correctionExplanation(),
                aiAudio,
                sttResult.accuracyScore(),
                sttResult.fluencyScore(),
                sttResult.completenessScore(),
                sttResult.pronunciationScore(),
                parsed.suggestedReplies());
    }

    @Override
    public PageData<SpeakingSessionListItemResult> getUserSessionHistories(SpeakingSessionFilterCommand command) {
        return speakingSessionRepositoryPort.findUserSessions(command);
    }

    @Override
    public SpeakingSessionDetailResult getSessionHistoryDetail(String sessionCode, Long userId) {
        return speakingSessionRepositoryPort.findSessionDetailByCode(sessionCode, userId)
                .orElseThrow(() -> new ApplicationException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND));
    }

    @Override
    public ActiveSpeakingSessionResult getActiveSession(Long userId, Integer personaId) {
        if (userId == null) {
            return null;
        }
        Long pId = (personaId != null && personaId > 0) ? personaId.longValue() : null;
        ActiveSpeakingSessionResult activeSession = speakingSessionRepositoryPort.findActiveSession(userId, pId)
                .orElse(null);
        if (activeSession != null) {
            speakingSessionHelper.ensureSessionLoadedInMemory(activeSession.sessionCode());
        }
        return activeSession;
    }

    @Override
    public StartConversationResult resumeSession(String sessionCode, Long userId) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_ID_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_ID_INVALID);
        }

        // 1. Nếu session đang còn trong memory
        if (sessionStorePort.hasSession(sessionCode)) {
            List<Map<String, String>> history = sessionStorePort.getConversationHistory(sessionCode);
            String lastAssistantReply = "";
            for (int i = history.size() - 1; i >= 0; i--) {
                if ("assistant".equals(history.get(i).get("role"))) {
                    lastAssistantReply = history.get(i).get("content");
                    break;
                }
            }
            String audioBase64 = speakingSessionHelper.toAudioBase64(sessionCode, lastAssistantReply);
            return new StartConversationResult(
                    sessionCode,
                    audioBase64,
                    lastAssistantReply,
                    "",
                    "");
        }

        // 2. Khôi phục từ DB nếu session bị mất trong memory
        ActiveSpeakingSessionResult activeSession = speakingSessionRepositoryPort
                .findActiveSessionByCode(sessionCode, userId)
                .orElseThrow(() -> new ApplicationException(
                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND));

        List<Map<String, String>> historyMessages = new ArrayList<>();
        String lastAssistantReply = "";

        if (activeSession.messages() != null) {
            for (var msg : activeSession.messages()) {
                historyMessages.add(Map.of("role", msg.senderType(), "content", msg.content()));
                if ("assistant".equals(msg.senderType())) {
                    lastAssistantReply = msg.content();
                }
            }
        }

        StringBuilder fullTranscript = new StringBuilder();
        if (activeSession.messages() != null) {
            for (var msg : activeSession.messages()) {
                fullTranscript.append("[Turn]\n").append(msg.senderType()).append(": ").append(msg.content())
                        .append("\n");
            }
        }

        sessionStorePort.restoreSession(
                sessionCode,
                userId,
                activeSession.personaId(),
                activeSession.topic(),
                activeSession.marugotoLevel(),
                activeSession.formalityLevel(),
                fullTranscript.toString(),
                activeSession.totalTurns(),
                activeSession.startedAt(),
                historyMessages);

        String audioBase64 = speakingSessionHelper.toAudioBase64(sessionCode, lastAssistantReply);

        return new StartConversationResult(
                sessionCode,
                audioBase64,
                lastAssistantReply,
                "",
                "");
    }
}
