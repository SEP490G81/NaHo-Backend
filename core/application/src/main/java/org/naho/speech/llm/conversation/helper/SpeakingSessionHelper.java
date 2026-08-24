package org.naho.speech.llm.conversation.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.persona.model.Persona;
import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.llm.conversation.constant.AiMessageField;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.internal.ParsedAiReply;
import org.naho.speech.llm.conversation.mapper.LevelPromptMapper;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.SenderType;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class SpeakingSessionHelper {

    public static final String SYSTEM_PROMPT_TEMPLATE_PATH = "/prompt_template/speaking_session_chat.prompt";

    public static final int MAX_SLIDING_WINDOW_MESSAGES = 16;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final TextToSpeechServicePort textToSpeechServicePort;
    private final LevelPromptMapper levelPromptMapper;

    public SpeakingSessionHelper(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            TextToSpeechServicePort textToSpeechServicePort,
            LevelPromptMapper levelPromptMapper
    ) {
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.textToSpeechServicePort = textToSpeechServicePort;
        this.levelPromptMapper = levelPromptMapper;
    }

    public ParsedAiReply parseAiResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR
            );
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
            String reply = root.path("reply").asText("");
            String replyTranslation = root.path("replyTranslation").asText("");
            String grammarNote = root.has("grammarNote")
                    ? root.path("grammarNote").asText("")
                    : root.path("grammarExplanation").asText("");
            String correctedUserText = root.path("correctedUserText").asText("");
            String correctionExplanation = root.path("correctionExplanation").asText("");
            String hintForLearner = root.path("hintForLearner").asText("");

            List<String> suggestedReplies = new ArrayList<>();
            JsonNode suggestionsNode = root.path("suggestedReplies");
            if (suggestionsNode.isArray()) {
                for (JsonNode item : suggestionsNode) {
                    suggestedReplies.add(item.asText(""));
                }
            }

            return ParsedAiReply.builder()
                    .reply(reply)
                    .replyTranslation(replyTranslation)
                    .correctedUserText(correctedUserText)
                    .correctionExplanation(correctionExplanation)
                    .hintForLearner(hintForLearner)
                    .grammarNote(grammarNote)
                    .suggestedReplies(suggestedReplies)
                    .build();

        } catch (Exception e) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage()
            );
        }
    }

    public String buildSystemPromptContent(
            Persona persona,
            FormalityLevel formalityLevel,
            MarugotoLevel marugotoLevel
    ) {
        return loadPromptTemplate()
                .replace("{{personaDescription}}", persona.getPrompt())
                .replace(
                        "{{formalityDescription}}",
                        levelPromptMapper.mapFormalityLevelToPrompt(formalityLevel)
                )
                .replace(
                        "{{marugotoDescription}}",
                        levelPromptMapper.mapMarugotoLevelToPrompt(marugotoLevel)
                )
                .replace("{{formalityLevel}}", formalityLevel.name())
                .replace("{{marugotoLevel}}", marugotoLevel.name());
    }

    public List<Map<String, String>> getSlidingWindowMessages(
            SpeakingSession speakingSession,
            Persona persona,
            List<SpeakingSessionMessage> previousMessages,
            Map<String, String> userMessage
    ) {
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

        sessionHistories.add(userMessage);

        String systemPromptContent = buildSystemPromptContent(
                persona,
                speakingSession.getFormalityLevel(),
                speakingSession.getMarugotoLevel()
        );

        Map<String, String> systemPrompt = new HashMap<>();
        systemPrompt.put(AiMessageField.ROLE, SenderType.SYSTEM.name().toLowerCase());
        systemPrompt.put(AiMessageField.CONTENT, systemPromptContent);

        sessionHistories.add(systemPrompt);

        return sessionHistories;
    }

    public String toAudioBase64(Long sessionId, String text) {
        try {
            SpeakingSession speakingSession = speakingSessionRepositoryPort.findBySessionId(sessionId);
            String voiceName = speakingSession.getVoiceName();
            byte[] audioBytes = textToSpeechServicePort.textToSpeech(text, voiceName, "ja-JP").audioData();
            return Base64.getEncoder().encodeToString(audioBytes);
        } catch (Exception e) {
            System.out.println("[SpeakingSessionHelper] TTS failed for session: " + e.getMessage());
            return null;
        }
    }

    public String loadPromptTemplate() {
        try (var inputStream = getClass().getResourceAsStream(SYSTEM_PROMPT_TEMPLATE_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("Prompt template not found: " + SYSTEM_PROMPT_TEMPLATE_PATH);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prompt template: " + SYSTEM_PROMPT_TEMPLATE_PATH, e);
        }
    }
}
