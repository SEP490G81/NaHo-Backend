package org.naho.speech.llm.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.out.AiChatPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.port.out.SpeechToTextPort;
import org.naho.speech.llm.result.*;


import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SpeakingSessionUseCase implements SpeakingSessionInputPort {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            You are a Japanese conversation partner on the NaHo language learning platform.
            %s
            
            ## CONVERSATION BEHAVIOR RULES
            1. **Language**: The "reply" field MUST be in Japanese ONLY. No English or Vietnamese in "reply".
            2. **Length calibration**:
               - Learner message ≤ 10 words → reply ≤ 2 sentences + 1 follow-up question.
               - Learner message > 10 words → reply 2–4 sentences.
               - NEVER write a wall of text. You are a conversation partner, not a lecturer.
            3. **Grammar error handling**:
               - If learner uses wrong particle, wrong verb conjugation, or unnatural phrasing:
                 → Subtly model the correct form naturally in your Japanese reply.
                 → Then fill correctedUserText + correctionExplanation fields.
               - Common errors to watch: は/が confusion, を/に confusion, plain vs polite form mismatch.
            4. **Stuck learner detection**:
               - If learner sends only fillers (あー, えーと, うーん) or ≤ 3 meaningful words:
                 → Your reply MUST include a simpler re-ask or a scaffolding hint.
                 → Example: 「少し難しかったですか？「〇〇は△△です」のように言えますよ。」
            5. **Topic steering**: Gently redirect off-topic responses. Stay on session topic.
            6. **If no grammar errors found**: correctionExplanation = "Câu của bạn đã rất tự nhiên và chính xác!"
            7. **Naturalness over perfection**: Prefer warm, natural Japanese over formal textbook phrases.
            
            ## OUTPUT FORMAT (MANDATORY)
            Respond ONLY with a valid raw JSON object. No markdown, no code fences. All 6 fields required:
            {
              "reply": "<Full Japanese response — naturally phrased>",
              "replyTranslation": "<Natural Vietnamese translation of reply>",
              "grammarNote": "<Vietnamese: Explain 1-2 grammar points/vocab used in YOUR reply>",
              "correctedUserText": "<Corrected Japanese of learner's last turn, or natural alternative if no error>",
              "correctionExplanation": "<Vietnamese: what was wrong and why correction is better, or praise if correct>",
              "hintForLearner": "<Optional Vietnamese tip for next turn, empty string \"\" if no tip>"
            }
            """;

    private static final String PERSONA_INSTRUCTION =
            "- You are roleplaying as the specified persona. Adapt your tone, formality, and personality accordingly.\n"
                    + "- Start by greeting the learner in character and inviting them to converse.";

    private final AiChatPort aiChatPort;
    private final SessionStorePort sessionStorePort;
    private final SpeechToTextPort speechToTextPort;
    private final PersonaRepositoryPort personaRepositoryPort;
    private final TextToSpeechServicePort textToSpeechServicePort;
    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;

    public SpeakingSessionUseCase(AiChatPort aiChatPort,
                                  SessionStorePort sessionStorePort,
                                  SpeechToTextPort speechToTextPort,
                                  PersonaRepositoryPort personaRepositoryPort,
                                  TextToSpeechServicePort textToSpeechServicePort,
                                  SpeakingSessionRepositoryPort speakingSessionRepositoryPort) {
        this.aiChatPort = aiChatPort;
        this.sessionStorePort = sessionStorePort;
        this.speechToTextPort = speechToTextPort;
        this.personaRepositoryPort = personaRepositoryPort;
        this.textToSpeechServicePort = textToSpeechServicePort;
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
    }

    private ParsedAiReply parseAiResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return new ParsedAiReply("", "", "", "", "", "");
        }
        try {
            String cleaned = rawResponse.trim();
            if (cleaned.startsWith("```")) {
                int firstNewline = cleaned.indexOf("\n");
                int lastBacktick = cleaned.lastIndexOf("```");
                if (firstNewline != -1 && lastBacktick > firstNewline) {
                    cleaned = cleaned.substring(firstNewline + 1, lastBacktick).trim();
                }
            }
            JsonNode root = OBJECT_MAPPER.readTree(cleaned);
            String reply = root.path("reply").asText(rawResponse);
            String replyTranslation = root.path("replyTranslation").asText("");
            // Support both old 'grammarExplanation' and new 'grammarNote' field names
            String grammarNote = root.has("grammarNote")
                    ? root.path("grammarNote").asText("")
                    : root.path("grammarExplanation").asText("");
            String correctedUserText = root.path("correctedUserText").asText("");
            String correctionExplanation = root.path("correctionExplanation").asText("");
            String hintForLearner = root.path("hintForLearner").asText("");
            return new ParsedAiReply(reply, replyTranslation, grammarNote, correctedUserText, correctionExplanation, hintForLearner);
        } catch (Exception e) {
            System.out.println("[SpeakingSessionUseCase] Fallback raw text parsing: " + e.getMessage());
            return new ParsedAiReply(rawResponse, "", "", "", "", "");
        }
    }

    @Override
    public ChatResult sendMessage(SendMessageWithSessionCommand command) {
        String sessionId = command.sessionId();
        ensureSessionLoadedInMemory(sessionId);
        String userMessage = command.userMessage();
        sessionStorePort.addMessage(sessionId, "user", userMessage);

        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionId);
        String rawReply = aiChatPort.chatWithContext(messages);
        ParsedAiReply parsed = parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionId, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + userMessage + "\nAssistant: " + parsed.reply() + "\n"
        );
        int currentTurn = sessionStorePort.incrementTurnCount(sessionId);

        // Real-time persistence to DB for session resume
        try {
            speakingSessionRepositoryPort.saveSessionMessage(sessionId, currentTurn, "user", userMessage, parsed.correctedUserText(), parsed.correctionExplanation(), null, parsed.hintForLearner(), null);
            speakingSessionRepositoryPort.saveSessionMessage(sessionId, currentTurn, "assistant", parsed.reply(), null, null, parsed.grammarNote(), null, null);
            speakingSessionRepositoryPort.updateSessionTurnAndTranscript(sessionId, currentTurn, sessionStorePort.getFullTranscript(sessionId));
        } catch (Exception e) {
            System.err.println("[SpeakingSessionUseCase] Failed to persist turn message to DB: " + e.getMessage());
        }

        String aiAudio = toAudioBase64(sessionId, parsed.reply());

        return new ChatResult(
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote(),
                parsed.correctedUserText(),
                parsed.correctionExplanation(),
                aiAudio
        );
    }

    @Override
    public void sendMessageStream(SendMessageWithSessionCommand command, Consumer<String> onToken) {
        String sessionId = command.sessionId();
        ensureSessionLoadedInMemory(sessionId);
        String userMessage = command.userMessage();
        sessionStorePort.addMessage(sessionId, "user", userMessage);

        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionId);
        StringBuilder fullReply = new StringBuilder();
        aiChatPort.chatStreamWithContext(messages, token -> {
            fullReply.append(token);
            onToken.accept(token);
        });

        String rawReply = fullReply.toString();
        ParsedAiReply parsed = parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionId, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + userMessage + "\nAssistant: " + parsed.reply() + "\n");
        sessionStorePort.incrementTurnCount(sessionId);
    }

    @Override
    public StartConversationResult startConversationWithAISession(StartSpeakingConversationWithAICommand startSpeakingConversationWithAICommand) {
        String sessionId = UUID.randomUUID().toString();
        Persona persona = personaRepositoryPort.findById((long) startSpeakingConversationWithAICommand.personaId())
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        PersonaDetailMessageKey.PERSONA_NOT_FOUND,
                        startSpeakingConversationWithAICommand.personaId()
                ));

        sessionStorePort.initSession(sessionId);
        sessionStorePort.setTopic(sessionId, "Conversation with " + persona.getName());
        sessionStorePort.setVoiceName(sessionId, "ja-JP-NanamiNeural");

        FormalityLevel effectiveFormality = startSpeakingConversationWithAICommand.formalityLevelOverride();
        if (effectiveFormality == null && persona.getConversationStyle() != null) {
            effectiveFormality = persona.getConversationStyle().getFormalityLevel();
        }

        MarugotoLevel effectiveMarugoto = startSpeakingConversationWithAICommand.marugotoLevelOverride();
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
                personaCtx.append("Style description: ").append(persona.getConversationStyle().getDescription()).append("\n");
            }
            if (persona.getConversationStyle().getPrompt() != null) {
                personaCtx.append("Style instructions: ").append(persona.getConversationStyle().getPrompt()).append("\n");
            }
        }
        if (effectiveFormality != null) {
            personaCtx.append("formalityLevel: ").append(effectiveFormality.name()).append("\n");
        }
        if (effectiveMarugoto != null) {
            personaCtx.append("marugotoLevel: ").append(effectiveMarugoto.name()).append("\n");
        }
        sessionStorePort.setPersonaContext(sessionId, personaCtx.toString());

        StringBuilder customInstruction = new StringBuilder(PERSONA_INSTRUCTION);
        customInstruction.append("\n- Your persona role & prompt: ").append(persona.getPrompt());
        if (persona.getConversationStyle() != null) {
            if (persona.getConversationStyle().getDescription() != null) {
                customInstruction.append("\n- Conversation style description: ").append(persona.getConversationStyle().getDescription());
            }
            if (persona.getConversationStyle().getPrompt() != null) {
                customInstruction.append("\n- Conversation style prompt: ").append(persona.getConversationStyle().getPrompt());
            }
        }
        if (effectiveFormality != null) {
            customInstruction.append("\n- Formality level (Keigo/Style): ").append(effectiveFormality.name());
        }
        if (effectiveMarugoto != null) {
            customInstruction.append("\n- Marugoto course level: ").append(effectiveMarugoto.name());
        }

        // Store session metadata for DB persistence
        sessionStorePort.setUserId(sessionId, startSpeakingConversationWithAICommand.userId());
        sessionStorePort.setPersonaId(sessionId, (long) startSpeakingConversationWithAICommand.personaId());
        if (effectiveMarugoto != null) {
            sessionStorePort.setMarugotoLevel(sessionId, effectiveMarugoto.name());
        }
        if (effectiveFormality != null) {
            sessionStorePort.setFormalityLevel(sessionId, effectiveFormality.name());
        }

        String prompt = SYSTEM_PROMPT_TEMPLATE.formatted(customInstruction.toString());
        sessionStorePort.addMessage(sessionId, "system", prompt);
        sessionStorePort.addMessage(sessionId, "user", "こんにちは、話しましょう！");

        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionId);
        String rawReply = aiChatPort.chatWithContext(messages);
        ParsedAiReply parsed = parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionId, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: こんにちは、話しましょう！\nAssistant: " + parsed.reply() + "\n"
        );
        sessionStorePort.incrementTurnCount(sessionId);

        // Create IN_PROGRESS session in DB
        try {
            speakingSessionRepositoryPort.createInProgressSession(
                    sessionId,
                    startSpeakingConversationWithAICommand.userId(),
                    (long) startSpeakingConversationWithAICommand.personaId(),
                    "Conversation with " + persona.getName(),
                    effectiveMarugoto != null ? effectiveMarugoto.name() : null,
                    effectiveFormality != null ? effectiveFormality.name() : null
            );
            speakingSessionRepositoryPort.saveSessionMessage(sessionId, 0, "assistant", parsed.reply(), null, null, parsed.grammarNote(), null, null);
        } catch (Exception e) {
            System.err.println("[SpeakingSessionUseCase] Failed to create IN_PROGRESS session in DB: " + e.getMessage());
        }

        String audioBase64 = toAudioBase64(sessionId, parsed.reply());

        return new StartConversationResult(
                sessionId,
                audioBase64,
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote()
        );
    }

    @Override
    public AudioChatResult sendAudioMessage(SendAudioMessageCommand command) {
        String sessionId = command.sessionId();
        ensureSessionLoadedInMemory(sessionId);

        SpeechToTextResult sttResult = speechToTextPort.transcribeAndAssess(
                command.audioBytes(), command.referenceText());

        String transcribedText = sttResult.transcribedText();
        System.out.println("[SpeakingSession] STT result: " + transcribedText);

        sessionStorePort.addMessage(sessionId, "user", transcribedText);

        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionId);
        String rawReply = aiChatPort.chatWithContext(messages);
        ParsedAiReply parsed = parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionId, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + transcribedText + "\nAssistant: " + parsed.reply() + "\n");
        int currentTurn = sessionStorePort.incrementTurnCount(sessionId);

        // Real-time persistence to DB for session resume
        try {
            speakingSessionRepositoryPort.saveSessionMessage(sessionId, currentTurn, "user", transcribedText, parsed.correctedUserText(), parsed.correctionExplanation(), null, parsed.hintForLearner(), sttResult.pronunciationScore());
            speakingSessionRepositoryPort.saveSessionMessage(sessionId, currentTurn, "assistant", parsed.reply(), null, null, parsed.grammarNote(), null, null);
            speakingSessionRepositoryPort.updateSessionTurnAndTranscript(sessionId, currentTurn, sessionStorePort.getFullTranscript(sessionId));
        } catch (Exception e) {
            System.err.println("[SpeakingSessionUseCase] Failed to persist audio message to DB: " + e.getMessage());
        }

        String aiAudio = toAudioBase64(sessionId, parsed.reply());

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
                sttResult.pronunciationScore()
        );
    }

    @Override
    public PageData<SpeakingSessionListItemResult> getUserSessionHistories(SpeakingSessionFilterCommand command) {
        return speakingSessionRepositoryPort.findUserSessions(command);
    }

    @Override
    public SpeakingSessionDetailResult getSessionHistoryDetail(String sessionCode, Long userId) {
        return speakingSessionRepositoryPort.findSessionDetailByCode(sessionCode, userId)
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        "Không tìm thấy thông tin buổi nói chuyện hoặc không có quyền truy cập."
                ));
    }

    @Override
    public ActiveSpeakingSessionResult getActiveSession(Long userId, Integer personaId) {
        if (userId == null) return null;
        Long pId = (personaId != null && personaId > 0) ? personaId.longValue() : null;
        ActiveSpeakingSessionResult activeSession = speakingSessionRepositoryPort.findActiveSession(userId, pId).orElse(null);
        if (activeSession != null) {
            ensureSessionLoadedInMemory(activeSession.sessionCode());
        }
        return activeSession;
    }

    @Override
    public StartConversationResult resumeSession(String sessionCode, Long userId) {
        if (sessionCode == null || sessionCode.isBlank()) {
            throw new ApplicationException(PersonaErrorCode.PERSONA_NOT_FOUND, "Mã phiên nói chuyện không hợp lệ.");
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
            ParsedAiReply parsed = parseAiResponse(lastAssistantReply);
            String audioBase64 = toAudioBase64(sessionCode, parsed.reply());
            return new StartConversationResult(
                    sessionCode,
                    audioBase64,
                    parsed.reply(),
                    parsed.replyTranslation(),
                    parsed.grammarNote()
            );
        }

        // 2. Khôi phục từ DB nếu session bị mất trong memory
        ActiveSpeakingSessionResult activeSession = speakingSessionRepositoryPort.findActiveSessionByCode(sessionCode, userId)
                .orElseThrow(() -> new ApplicationException(
                        PersonaErrorCode.PERSONA_NOT_FOUND,
                        "Không tìm thấy phiên nói chuyện dở dang để tiếp tục."
                ));

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
                fullTranscript.append("[Turn]\n").append(msg.senderType()).append(": ").append(msg.content()).append("\n");
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
                historyMessages
        );

        ParsedAiReply parsed = parseAiResponse(lastAssistantReply);
        String audioBase64 = toAudioBase64(sessionCode, parsed.reply());

        return new StartConversationResult(
                sessionCode,
                audioBase64,
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarNote()
        );
    }

    private String toAudioBase64(String sessionId, String text) {
        try {
            String voiceName = sessionStorePort.getVoiceName(sessionId);
            byte[] audioBytes = textToSpeechServicePort.textToSpeech(text, voiceName, "ja-JP").audioData();
            return Base64.getEncoder().encodeToString(audioBytes);
        } catch (Exception e) {
            System.out.println("[SpeakingSession] TTS failed for session " + sessionId + ": " + e.getMessage());
            return null;
        }
    }

    private void ensureSessionLoadedInMemory(String sessionCode) {
        if (sessionCode == null || sessionCode.isBlank()) {
            return;
        }

        if (sessionStorePort.hasSession(sessionCode)) {
            return;
        }

        ActiveSpeakingSessionResult activeSession = speakingSessionRepositoryPort.findActiveSessionByCode(sessionCode, null)
                .orElse(null);

        if (activeSession == null) {
            return;
        }

        List<Map<String, String>> historyMessages = new ArrayList<>();
        if (activeSession.messages() != null) {
            for (var msg : activeSession.messages()) {
                historyMessages.add(Map.of("role", msg.senderType(), "content", msg.content()));
            }
        }

        StringBuilder fullTranscript = new StringBuilder();
        if (activeSession.messages() != null) {
            for (var msg : activeSession.messages()) {
                fullTranscript.append("[Turn]\n").append(msg.senderType()).append(": ").append(msg.content()).append("\n");
            }
        }

        sessionStorePort.restoreSession(
                sessionCode,
                null,
                activeSession.personaId(),
                activeSession.topic(),
                activeSession.marugotoLevel(),
                activeSession.formalityLevel(),
                fullTranscript.toString(),
                activeSession.totalTurns(),
                activeSession.startedAt(),
                historyMessages
        );
    }

    private record ParsedAiReply(
            String reply,
            String replyTranslation,
            String grammarNote,
            String correctedUserText,
            String correctionExplanation,
            String hintForLearner
    ) {
    }
}
