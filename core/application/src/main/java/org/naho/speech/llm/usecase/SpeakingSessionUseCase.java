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
import org.naho.speech.llm.command.*;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.out.AiChatPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.port.out.SpeechToTextPort;
import org.naho.speech.llm.result.*;

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

    private static final String TOPIC_INSTRUCTION =
            "- The conversation topic is: 「%s」. Stay on this topic.\n"
                    + "- Start with a warm greeting related to this topic.";

    private static final String FREE_INSTRUCTION =
            "- This is a free conversation. The learner can talk about any topic.\n"
                    + "- Start by greeting the learner and asking what they'd like to talk about.";

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
    public SpeakingTopicResult startTopicSession(StartSpeakingTopicCommand command) {
        String sessionId = UUID.randomUUID().toString();
        String topic = command.topic();
        sessionStorePort.initSession(sessionId);
        sessionStorePort.setTopic(sessionId, topic);
        sessionStorePort.setVoiceName(sessionId, "ja-JP-NanamiNeural");
        sessionStorePort.setSessionType(sessionId, "TOPIC");

        String prompt = SYSTEM_PROMPT_TEMPLATE.formatted(TOPIC_INSTRUCTION.formatted(topic));
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

        String audioBase64 = toAudioBase64(sessionId, parsed.reply());
        System.out.println("[SpeakingSession] Topic session started: " + sessionId + " | Topic: " + topic);

        return new SpeakingTopicResult(
                sessionId,
                topic,
                parsed.reply(),
                audioBase64,
                parsed.replyTranslation(),
                parsed.grammarNote()
        );
    }

    @Override
    public ChatResult sendMessage(SendMessageWithSessionCommand command) {
        String sessionId = command.sessionId();
        String userMessage = command.userMessage();
        sessionStorePort.addMessage(sessionId, "user", userMessage);

        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionId);
        String rawReply = aiChatPort.chatWithContext(messages);
        ParsedAiReply parsed = parseAiResponse(rawReply);

        sessionStorePort.addMessage(sessionId, "assistant", parsed.reply());
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + userMessage + "\nAssistant: " + parsed.reply() + "\n"
        );
        sessionStorePort.incrementTurnCount(sessionId);

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

        StringBuilder customInstruction = new StringBuilder(FREE_INSTRUCTION);
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
        sessionStorePort.setSessionType(sessionId, "PERSONA");
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
        sessionStorePort.incrementTurnCount(sessionId);

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
