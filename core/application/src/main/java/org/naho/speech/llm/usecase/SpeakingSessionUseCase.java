package org.naho.speech.llm.usecase;

import org.naho.file.constant.FileAccessStatus;
import org.naho.file.model.File;
import org.naho.file.port.in.UploadFileInputPort;
import org.naho.file.port.out.FileRepositoryPort;
import org.naho.file.result.StoredFile;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.pagination.PageData;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.SpeakingSessionFilterCommand;
import org.naho.speech.llm.command.StartSpeakingConversationCommand;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.llm.helper.SpeakingSessionHelper;
import org.naho.speech.llm.helper.SpeakingSessionHelper.ParsedAiReply;
import org.naho.speech.llm.mapper.SpeakingSessionResultMapper;
import org.naho.speech.llm.model.SpeakingSession;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.out.AiChatPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.port.out.SpeechToTextPort;
import org.naho.speech.llm.result.*;
import org.naho.speech.llm.type.MessageType;
import org.naho.speech.llm.type.SpeakingSessionStatus;
import org.naho.speech.llm.validator.SessionValidator;
import org.naho.user.exception.UserErrorCode;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class SpeakingSessionUseCase implements SpeakingSessionInputPort {

    private final AiChatPort aiChatPort;
    private final SessionStorePort sessionStorePort;
    private final SpeechToTextPort speechToTextPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final FileRepositoryPort fileRepositoryPort;
    private final UploadFileInputPort uploadFileInputPort;
    private final SessionValidator sessionValidator;
    private final SpeakingSessionHelper speakingSessionHelper;
    private final SpeakingSessionResultMapper speakingSessionResultMapper;
    private final TransactionPort transactionPort;

    public SpeakingSessionUseCase(
            AiChatPort aiChatPort,
            SessionStorePort sessionStorePort,
            SpeechToTextPort speechToTextPort,
            PersonaRepositoryPort personaRepositoryPort,
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            FileRepositoryPort fileRepositoryPort,
            UploadFileInputPort uploadFileInputPort,
            SessionValidator sessionValidator,
            SpeakingSessionHelper speakingSessionHelper,
            SpeakingSessionResultMapper speakingSessionResultMapper,
            TransactionPort transactionPort
    ) {
        this.aiChatPort = aiChatPort;
        this.sessionStorePort = sessionStorePort;
        this.speechToTextPort = speechToTextPort;
        this.personaRepositoryPort = personaRepositoryPort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.fileRepositoryPort = fileRepositoryPort;
        this.uploadFileInputPort = uploadFileInputPort;
        this.sessionValidator = sessionValidator;
        this.speakingSessionHelper = speakingSessionHelper;
        this.speakingSessionResultMapper = speakingSessionResultMapper;
        this.transactionPort = transactionPort;
    }

    @Override
    public String startConversation(StartSpeakingConversationCommand command) {
        // Kiểm tra xem người dùng đã tới giới hạn lượt tạo session trong ngày chưa
        sessionValidator.validateSessionStartLimit(command.userId());

        // Kiểm tra xem người dùng đã tới giới hạn số session đang mở chưa
        sessionValidator.validateMaxInProgressSession(command.userId());

        String sessionCode = UUID.randomUUID().toString();

        Persona persona = personaRepositoryPort.findById(command.personaId())
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        command.personaId())
                );

        // Lưu vào redis
        sessionStorePort.initSession(sessionCode);
        sessionStorePort.setTopic(sessionCode, "Conversation with " + persona.getName());
        sessionStorePort.setVoiceName(sessionCode, "ja-JP-NanamiNeural");

        FormalityLevel effectiveFormality = command.formalityLevelOverride();
        if (effectiveFormality == null && persona.getConversationStyle() != null) {
            effectiveFormality = persona.getConversationStyle().getFormalityLevel();
        }

        MarugotoLevel effectiveMarugoto = command.marugotoLevelOverride();
        if (effectiveMarugoto == null && persona.getConversationStyle() != null) {
            effectiveMarugoto = persona.getConversationStyle().getMarugotoLevel();
        }

        sessionStorePort.setUserId(sessionCode, command.userId());
        sessionStorePort.setPersonaId(sessionCode, command.personaId());

        if (effectiveMarugoto != null) {
            sessionStorePort.setMarugotoLevel(sessionCode, effectiveMarugoto);
        }

        if (effectiveFormality != null) {
            sessionStorePort.setFormalityLevel(sessionCode, effectiveFormality);
        }

        // tạo mới và lưu speaking session trong db
        speakingSessionRepositoryPort.createInProgressSession(
                sessionCode,
                command.userId(),
                command.personaId(),
                "Conversation with " + persona.getName(),
                "ja-JP-NanamiNeural",
                effectiveMarugoto,
                effectiveFormality
        );

        return sessionCode;
    }

    @Override
    public StartConversationResult initFirstGreeting(String sessionCode, Long userId) {
        // kiểm tra xem session có thuộc về user không
        sessionValidator.validateSessionIsBelongToUser(sessionCode, userId);

        // kiểm tra xem session đã start chưa, nếu đã start thì ném ra lỗi
        sessionValidator.validateSessionIsNotStarted(sessionCode);

        SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);

        Persona persona = personaRepositoryPort.findById(speakingSession.getPersonaId())
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        speakingSession.getPersonaId())
                );

        StringBuilder personaContext = new StringBuilder();
        personaContext.append("Persona name: ").append(persona.getName()).append("\n");
        if (persona.getPrompt() != null) {
            personaContext.append("Persona role: ").append(persona.getPrompt()).append("\n");
        }
        if (persona.getConversationStyle() != null) {
            if (persona.getConversationStyle().getDescription() != null) {
                personaContext.append("Style description: ").append(persona.getConversationStyle().getDescription())
                        .append("\n");
            }
            if (persona.getConversationStyle().getPrompt() != null) {
                personaContext.append("Style instructions: ").append(persona.getConversationStyle().getPrompt())
                        .append("\n");
            }
        }

        FormalityLevel effectiveFormality = speakingSession.getFormalityLevel();
        MarugotoLevel effectiveMarugoto = speakingSession.getMarugotoLevel();

        if (effectiveFormality != null) {
            personaContext.append("formalityLevel: ").append(effectiveFormality.name()).append("\n");
        }

        if (effectiveMarugoto != null) {
            personaContext.append("marugotoLevel: ").append(effectiveMarugoto.name()).append("\n");
        }

        sessionStorePort.setPersonaContext(sessionCode, personaContext.toString());

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

        String prompt = SpeakingSessionHelper.SYSTEM_PROMPT_TEMPLATE.formatted(customInstruction.toString());
        sessionStorePort.addMessage(sessionCode, "system", prompt);
        sessionStorePort.addMessage(sessionCode, "user", "こんにちは、話しましょう！");

        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionCode);
        String rawReply = aiChatPort.chatWithContext(messages);
        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionCode, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionCode, "[Turn]\nUser: こんにちは、話しましょう！\nAssistant: " + parsed.reply() + "\n");

        speakingSessionRepositoryPort.saveSessionMessage(
                sessionCode,
                0,
                "assistant",
                MessageType.TEXT,
                parsed.reply(),
                parsed.replyTranslation(),
                null,
                null,
                parsed.grammarNote(),
                null,
                null
        );

        String audioBase64 = speakingSessionHelper.toAudioBase64(speakingSession.getId(), parsed.reply());

        return new StartConversationResult(
                sessionCode,
                audioBase64,
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote()
        );
    }

    @Override
    public ChatResult sendMessage(SendMessageWithSessionCommand command) {
        String sessionCode = command.sessionCode();

        SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);

        sessionValidator.validateSessionNotCompleted(sessionCode);
        sessionValidator.validateSessionTurnLimit(sessionCode, null);

//        speakingSessionHelper.ensureSessionLoadedInMemory(sessionCode);

        String userMessage = command.userMessage();
        sessionStorePort.addMessage(sessionCode, "user", userMessage);

        List<Map<String, String>> messages = speakingSessionHelper.getSlidingWindowMessages(sessionCode);
        String rawReply = aiChatPort.chatWithContext(messages);

        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionCode, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionCode,
                "[Turn]\nUser: " + userMessage + "\nAssistant: " + parsed.reply() + "\n");
        int currentTurn = sessionStorePort.incrementTurnCount(sessionCode);

        // Async persistence to DB via Virtual Threads to optimize turn latency
        final String sCode = sessionCode;
        final int sTurn = currentTurn;
        final String uMsg = userMessage;
        final String aMsg = parsed.reply();
        final String aTrans = parsed.replyTranslation();
        final String cText = parsed.correctedUserText();
        final String cExp = parsed.correctionExplanation();
        final String gNote = parsed.grammarNote();
        final String hLearner = parsed.hintForLearner();
        final String fTranscript = sessionStorePort.getFullTranscript(sessionCode);

        Thread.ofVirtual().start(() -> {
            try {
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "user", MessageType.TEXT, uMsg, null,
                        cText, cExp, null, hLearner, null);
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "assistant", MessageType.TEXT, aMsg, aTrans, null,
                        null, gNote, null, null);
                speakingSessionRepositoryPort.updateSessionTurnAndTranscript(sCode, sTurn, fTranscript);
            } catch (Exception e) {
                System.out.println("[SpeakingSession] Error saving session message: " + e.getMessage());
                throw new ApplicationException(
                        LlmApplicationError.LLM_SAVE_SESSION_FAILED,
                        LlmDetailMessageKey.LLM_SAVE_SESSION_FAILED,
                        e.getMessage()
                );
            }
        });

        String aiAudio = speakingSessionHelper.toAudioBase64(speakingSession.getId(), parsed.reply());

        return new ChatResult(
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote(),
                parsed.correctedUserText(),
                parsed.correctionExplanation(),
                aiAudio
        );
    }

//    @Override
//    public void sendMessageStream(SendMessageWithSessionCommand command, Consumer<String> onToken) {
//        String sessionCode = command.sessionCode();
//        sessionValidator.validateSessionNotCompleted(sessionCode);
//        sessionValidator.validateSessionTurnLimit(sessionCode, null);
//        speakingSessionHelper.ensureSessionLoadedInMemory(sessionCode);
//        String userMessage = command.userMessage();
//        sessionStorePort.addMessage(sessionCode, "user", userMessage);
//
//        List<Map<String, String>> messages = speakingSessionHelper.getSlidingWindowMessages(sessionCode);
//        StringBuilder fullReply = new StringBuilder();
//        aiChatPort.chatStreamWithContext(messages, token -> {
//            fullReply.append(token);
//            onToken.accept(token);
//        });
//
//        String rawReply = fullReply.toString();
//        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);
//
//        sessionStorePort.addMessage(sessionCode, "assistant", parsed.reply());
//        sessionStorePort.appendTranscript(sessionCode,
//                "[Turn]\nUser: " + userMessage + "\nAssistant: " + parsed.reply() + "\n");
//        sessionStorePort.incrementTurnCount(sessionCode);
//    }

    @Override
    public AudioChatResult sendAudioMessage(SendAudioMessageCommand command) {
        String sessionCode = command.sessionCode();
        sessionValidator.validateSessionNotCompleted(sessionCode);
        sessionValidator.validateSessionTurnLimit(sessionCode, command.userId());

        SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionCode(sessionCode);
//        speakingSessionHelper.ensureSessionLoadedInMemory(sessionCode);

        SpeechToTextResult sttResult = speechToTextPort.transcribeAndAssess(
                command.audioBytes(), command.referenceText());

        String transcribedText = sttResult.transcribedText();
        System.out.println("[SpeakingSession] STT result: " + transcribedText);

        sessionStorePort.addMessage(sessionCode, "user", transcribedText);

        List<Map<String, String>> messages = speakingSessionHelper.getSlidingWindowMessages(sessionCode);
        String rawReply = aiChatPort.chatWithContext(messages);

        for (Map<String, String> m : messages) {
            System.out.println("[SpeakingSession] Message: " + m.get("content"));
        }

        ParsedAiReply parsed = speakingSessionHelper.parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionCode, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionCode,
                "[Turn]\nUser: " + transcribedText + "\nAssistant: " + parsed.reply() + "\n");
        int currentTurn = sessionStorePort.incrementTurnCount(sessionCode);

        // Async persistence to DB via Virtual Threads to optimize turn latency
        final String sCode = sessionCode;
        final int sTurn = currentTurn;
        final String uMsg = transcribedText;
        final String aMsg = parsed.reply();
        final String aTrans = parsed.replyTranslation();
        final String cText = parsed.correctedUserText();
        final String cExp = parsed.correctionExplanation();
        final String gNote = parsed.grammarNote();
        final String hLearner = parsed.hintForLearner();
        final Double pronScore = sttResult.pronunciationScore();
        final String fTranscript = sessionStorePort.getFullTranscript(sessionCode);

        final StoredFile storedFile = command.storedFile();

        Thread.ofVirtual().start(() -> {
            try {
                File audioFile = null;
                if (storedFile != null) {
                    audioFile = fileRepositoryPort.createNewForUpload(storedFile, FileAccessStatus.PRIVATE);
                }
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "user", MessageType.AUDIO, uMsg, null,
                        cText, cExp, null, hLearner, pronScore, audioFile);
                speakingSessionRepositoryPort.saveSessionMessage(sCode, sTurn, "assistant", MessageType.AUDIO, aMsg, aTrans, null,
                        null, gNote, null, null);
                speakingSessionRepositoryPort.updateSessionTurnAndTranscript(sCode, sTurn, fTranscript);

                if (storedFile != null) {
                    uploadFileInputPort.uploadFileToCloud(storedFile);
                }
            } catch (Exception e) {
                throw new ApplicationException(
                        LlmApplicationError.LLM_SAVE_SESSION_FAILED,
                        LlmDetailMessageKey.LLM_SAVE_SESSION_FAILED,
                        e.getMessage()
                );
            }
        });

        String aiAudio = speakingSessionHelper.toAudioBase64(speakingSession.getId(), parsed.reply());

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
    public SpeakingSessionResult getInProgressSessionDetails(String sessionCode, Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID
            );
        }

        // Kiểm tra xem session có thuộc về user không
        sessionValidator.validateSessionIsBelongToUser(sessionCode, userId);

        SpeakingSession inProgressSession = speakingSessionRepositoryPort
                .findBySessionCodeAndStatus(sessionCode, SpeakingSessionStatus.IN_PROGRESS);

//        speakingSessionHelper.ensureSessionLoadedInMemory(inProgressSession.getSessionCode());

        return speakingSessionResultMapper.domainToResult(inProgressSession);
    }

    @Override
    public List<SpeakingSessionResult> findAllInProgressSessionsByUserId(Long userId) {
        if (userId == null) {
            throw new ApplicationException(
                    UserErrorCode.USER_NOT_FOUND,
                    UserDetailMessageKey.USER_ID_NULL
            );
        }

        List<SpeakingSession> inProgressSessions = speakingSessionRepositoryPort.findAllInProgressSessionsByUserId(userId);
        return inProgressSessions.stream()
                .map(speakingSessionResultMapper::domainToResult)
                .toList();
    }

    //    @Override
//    public StartConversationResult resumeSession(String sessionCode, Long userId) {
//        if (sessionCode == null || sessionCode.isBlank()) {
//            throw new ApplicationException(
//                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
//                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID);
//        }
//
//        // 1. Nếu session đang còn trong memory
//        if (sessionStorePort.hasSession(sessionCode)) {
//            List<Map<String, String>> history = sessionStorePort.getConversationHistory(sessionCode);
//            String lastAssistantReply = "";
//            for (int i = history.size() - 1; i >= 0; i--) {
//                if ("assistant".equals(history.get(i).get("role"))) {
//                    lastAssistantReply = history.get(i).get("content");
//                    break;
//                }
//            }
//            String audioBase64 = speakingSessionHelper.toAudioBase64(sessionCode, lastAssistantReply);
//            return new StartConversationResult(
//                    sessionCode,
//                    audioBase64,
//                    lastAssistantReply,
//                    "",
//                    "");
//        }
//
//        // 2. Khôi phục từ DB nếu session bị mất trong memory
//        SpeakingSessionResult activeSession = speakingSessionRepositoryPort
//                .findActiveSessionByCode(sessionCode, userId)
//                .orElseThrow(() -> new ApplicationException(
//                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
//                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND));
//
//        List<Map<String, String>> historyMessages = new ArrayList<>();
//        String lastAssistantReply = "";
//
//        if (activeSession.messages() != null) {
//            for (var msg : activeSession.messages()) {
//                String role = msg.senderType() != null ? msg.senderType().toLowerCase() : "user";
//                historyMessages.add(Map.of("role", role, "content", msg.content()));
//                if ("assistant".equalsIgnoreCase(msg.senderType())) {
//                    lastAssistantReply = msg.content();
//                }
//            }
//        }
//
//        StringBuilder fullTranscript = new StringBuilder();
//        if (activeSession.messages() != null) {
//            for (var msg : activeSession.messages()) {
//                String role = msg.senderType() != null ? msg.senderType().toLowerCase() : "user";
//                fullTranscript.append("[Turn]\n").append(role).append(": ").append(msg.content())
//                        .append("\n");
//            }
//        }
//
//        sessionStorePort.restoreSession(
//                sessionCode,
//                userId,
//                activeSession.personaId(),
//                activeSession.topic(),
//                activeSession.marugotoLevel(),
//                activeSession.formalityLevel(),
//                fullTranscript.toString(),
//                activeSession.totalTurns(),
//                activeSession.startedAt(),
//                historyMessages);
//
//        String audioBase64 = speakingSessionHelper.toAudioBase64(sessionCode, lastAssistantReply);
//
//        return new StartConversationResult(
//                sessionCode,
//                audioBase64,
//                lastAssistantReply,
//                "",
//                "");
//    }
}
