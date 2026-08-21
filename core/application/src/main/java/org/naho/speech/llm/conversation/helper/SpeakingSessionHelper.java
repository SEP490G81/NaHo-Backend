package org.naho.speech.llm.conversation.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.port.out.SpeakingSessionRepositoryPort;
import org.naho.speech.llm.model.conversation.SpeakingSession;

import java.util.*;

public class SpeakingSessionHelper {

    public static final String SYSTEM_PROMPT_TEMPLATE = """
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
            8. **Reply suggestions**: Provide exactly 3 short, natural Japanese response options in "suggestedReplies" for the learner to choose from if they don't know what to reply next.
            
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

    public static final String PERSONA_INSTRUCTION = "- You are roleplaying as the specified persona. Adapt your tone, formality, and personality accordingly.\n"
            + "- Start by greeting the learner in character and inviting them to converse.";

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
            return new ParsedAiReply("", "", "", "", "", "", List.of());
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

            return new ParsedAiReply(
                    reply,
                    replyTranslation,
                    grammarNote,
                    correctedUserText,
                    correctionExplanation,
                    hintForLearner,
                    suggestedReplies
            );
        } catch (Exception e) {
            throw new ApplicationException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage()
            );
        }
    }

    public String buildPersonaContext(org.naho.persona.model.Persona persona, org.naho.persona.type.FormalityLevel formalityLevel, org.naho.persona.type.MarugotoLevel marugotoLevel) {
        if (persona == null) {
            return "";
        }
        StringBuilder personaContext = new StringBuilder();
        personaContext.append("Persona name: ").append(persona.getName()).append("\n");
        if (persona.getPrompt() != null) {
            personaContext.append("Persona role: ").append(persona.getPrompt()).append("\n");
        }
//        if (persona.getConversationStyle() != null) {
//            if (persona.getConversationStyle().getDescription() != null) {
//                personaContext.append("Style description: ").append(persona.getConversationStyle().getDescription())
//                        .append("\n");
//            }
//            if (persona.getConversationStyle().getPrompt() != null) {
//                personaContext.append("Style instructions: ").append(persona.getConversationStyle().getPrompt())
//                        .append("\n");
//            }
//        }

        if (formalityLevel != null) {
            personaContext.append("formalityLevel: ").append(formalityLevel.name()).append("\n");
        }

        if (marugotoLevel != null) {
            personaContext.append("marugotoLevel: ").append(marugotoLevel.name()).append("\n");
        }
        return personaContext.toString();
    }

    public String buildCustomInstruction(org.naho.persona.model.Persona persona, org.naho.persona.type.FormalityLevel formalityLevel, org.naho.persona.type.MarugotoLevel marugotoLevel) {
        StringBuilder customInstruction = new StringBuilder(PERSONA_INSTRUCTION);
        if (persona != null) {
            if (persona.getPrompt() != null) {
                customInstruction.append("\n- Your persona role & prompt: ").append(persona.getPrompt());
            }
//            if (persona.getConversationStyle() != null) {
//                if (persona.getConversationStyle().getDescription() != null) {
//                    customInstruction.append("\n- Conversation style description: ")
//                            .append(persona.getConversationStyle().getDescription());
//                }
//                if (persona.getConversationStyle().getPrompt() != null) {
//                    customInstruction.append("\n- Conversation style prompt: ")
//                            .append(persona.getConversationStyle().getPrompt());
//                }
//            }
        }

        if (formalityLevel != null) {
            customInstruction.append("\n- Formality level (Keigo/Style): ").append(formalityLevel.name());
        }

        if (marugotoLevel != null) {
            customInstruction.append("\n- Marugoto course level: ").append(marugotoLevel.name());
        }
        return customInstruction.toString();
    }

    public List<Map<String, String>> getSlidingWindowMessages(
            SpeakingSession speakingSession,
            org.naho.persona.model.Persona persona,
            List<org.naho.speech.llm.model.conversation.SpeakingSessionMessage> messages
    ) {
        List<Map<String, String>> history = new ArrayList<>();
        if (messages != null && !messages.isEmpty()) {
            List<org.naho.speech.llm.model.conversation.SpeakingSessionMessage> recentMessages;
            if (messages.size() > MAX_SLIDING_WINDOW_MESSAGES) {
                recentMessages = messages.subList(messages.size() - MAX_SLIDING_WINDOW_MESSAGES, messages.size());
            } else {
                recentMessages = messages;
            }
            for (org.naho.speech.llm.model.conversation.SpeakingSessionMessage msg : recentMessages) {
                String role = msg.getSenderType() != null ? msg.getSenderType().toLowerCase() : "user";
                history.add(Map.of("role", role, "content", msg.getContent() != null ? msg.getContent() : ""));
            }
        }

        org.naho.persona.type.FormalityLevel formality = speakingSession != null ? speakingSession.getFormalityLevel() : null;
        org.naho.persona.type.MarugotoLevel marugoto = speakingSession != null ? speakingSession.getMarugotoLevel() : null;

        String customInstruction = buildCustomInstruction(persona, formality, marugoto);
        String formattedSystemPrompt = SYSTEM_PROMPT_TEMPLATE.formatted(customInstruction);

        Map<String, String> systemPrompt = new HashMap<>();
        systemPrompt.put("role", "system");
        systemPrompt.put("content", formattedSystemPrompt);

        List<Map<String, String>> slidingWindow = new ArrayList<>();
        slidingWindow.add(systemPrompt);
        slidingWindow.addAll(history);
        return slidingWindow;
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

    public record ParsedAiReply(
            String reply,
            String replyTranslation,
            String grammarNote,
            String correctedUserText,
            String correctionExplanation,
            String hintForLearner,
            List<String> suggestedReplies
    ) {
    }

//    public void ensureSessionLoadedInMemory(String sessionCode) {
//        if (sessionCode == null || sessionCode.isBlank()) {
//            throw new ApplicationException(
//                    LlmApplicationError.LLM_SESSION_CODE_INVALID,
//                    LlmDetailMessageKey.LLM_SESSION_CODE_INVALID);
//        }
//
//        if (sessionStorePort.hasSession(sessionCode)) {
//            return;
//        }
//
//        SpeakingSessionResult activeSession = speakingSessionRepositoryPort
//                .findActiveSessionByCode(sessionCode, null)
//                .orElseThrow(() -> new ApplicationException(
//                        LlmApplicationError.LLM_SESSION_NOT_FOUND,
//                        LlmDetailMessageKey.LLM_SESSION_NOT_FOUND));
//
//        List<Map<String, String>> historyMessages = new ArrayList<>();
//        if (activeSession.messages() != null) {
//            for (var msg : activeSession.messages()) {
//                String role = msg.senderType() != null ? msg.senderType().toLowerCase() : "user";
//                historyMessages.add(Map.of("role", role, "content", msg.content()));
//            }
//        }
//
//        StringBuilder fullTranscript = new StringBuilder();
//        if (activeSession.messages() != null) {
//            for (var msg : activeSession.messages()) {
//                String role = msg.senderType() != null ? msg.senderType().toLowerCase() : "user";
//                fullTranscript.append("[Turn]\n")
//                        .append(role)
//                        .append(": ")
//                        .append(msg.content())
//                        .append("\n");
//            }
//        }
//
//        sessionStorePort.restoreSession(
//                sessionCode,
//                null,
//                activeSession.personaId(),
//                activeSession.topic(),
//                activeSession.marugotoLevel(),
//                activeSession.formalityLevel(),
//                fullTranscript.toString(),
//                activeSession.totalTurns(),
//                activeSession.startedAt(),
//                historyMessages);
//    }
}
