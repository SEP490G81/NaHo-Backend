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
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.model.conversation.SpeakingSession;
import org.naho.speech.llm.model.conversation.SpeakingSessionMessage;
import org.naho.speech.llm.type.SenderType;

import java.util.*;

public class SpeakingSessionHelper {

    public static final String SYSTEM_PROMPT_TEMPLATE = """
            ## YOUR ROLE
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
               - **Spoken Text & Punctuation**: Learner input is transcribed from speech (Speech-to-Text). Do NOT correct punctuation marks (like '.', ',', '。', '、', '?') or treat missing/extra punctuation as grammar mistakes. Evaluate ONLY spoken language grammar and phrasing.
            4. **Natural Aizuchi (相槌)**:
               - Use natural, context-appropriate Japanese conversational reactions (Aizuchi such as 「そうですね」「なるほど」「ええ」「へえ、そうですか」「あ、本当ですか」) occasionally and naturally to make the dialogue authentic and engaging.
            5. **Stuck learner detection**:
               - If learner sends only fillers (あー, えーと, うーん) or ≤ 3 meaningful words:
                 → Your reply MUST include a simpler re-ask or a scaffolding hint.
                 → Example: 「少し難しかったですか？「〇〇は△△です」のように言えますよ。」
            6. **Topic steering**: Gently redirect off-topic responses. Stay on session topic.
            7. **If no grammar errors found**: correctionExplanation = "Câu của bạn đã rất tự nhiên và chính xác!"
            8. **Naturalness over perfection**: Prefer warm, natural Japanese over formal textbook phrases.
            9. **Reply suggestions**: Provide exactly 3 short, natural Japanese response options in "suggestedReplies" for the learner to choose from if they don't know what to reply next.
            
            ## OUTPUT FORMAT (MANDATORY)
            Respond ONLY with a valid raw JSON object. No markdown, no code fences. All 7 fields required:
            {
              "reply": "<Full Japanese response — naturally phrased>",
              "replyTranslation": "<Natural Vietnamese translation of reply>",
              "grammarNote": "<Vietnamese: Explain 1-2 grammar points/vocab used in YOUR reply>",
              "correctedUserText": "<Corrected Japanese of learner's last turn, or natural alternative if no error>",
              "correctionExplanation": "<Vietnamese: what was wrong and why correction is better, or praise if correct>",
              "hintForLearner": "<Optional Vietnamese tip for next turn, empty string \\"\\" if no tip>",
              "suggestedReplies": [
                "<Short Japanese reply option 1 for learner>",
                "<Short Japanese reply option 2 for learner>",
                "<Short Japanese reply option 3 for learner>"
              ]
            }
            """;

    public static final String PERSONA_INSTRUCTION = """
            - You are roleplaying as the specified persona. Adapt your tone, formality, and personality accordingly.
            - Start by greeting the learner in character and inviting them to converse.
            - Your persona role & prompt: {{personaPrompt}}
            - Formality level (Keigo/Style): {{formality}}
            - Marugoto course level: {{marugoto}}
            """;

    public static final int MAX_SLIDING_WINDOW_MESSAGES = 16;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final SpeakingSessionRepositoryPort speakingSessionRepositoryPort;
    private final TextToSpeechServicePort textToSpeechServicePort;

    public SpeakingSessionHelper(
            SpeakingSessionRepositoryPort speakingSessionRepositoryPort,
            TextToSpeechServicePort textToSpeechServicePort
    ) {
        this.speakingSessionRepositoryPort = speakingSessionRepositoryPort;
        this.textToSpeechServicePort = textToSpeechServicePort;
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
        String personaContextPrompt = PERSONA_INSTRUCTION
                .replace("{{personaPrompt}}", persona.getPrompt())
                .replace("{{formality}}", formalityLevel.name())
                .replace("{{marugoto}}", marugotoLevel.name());

        return SYSTEM_PROMPT_TEMPLATE.formatted(personaContextPrompt);
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
}
