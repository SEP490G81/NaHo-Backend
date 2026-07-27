package org.naho.speech.llm.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.i18n.message.persona.PersonaDetailMessageKey;
import org.naho.persona.exception.PersonaErrorCode;
import org.naho.persona.model.Persona;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.port.out.TextToSpeechServicePort;
import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.StartSpeakingConversationWithAICommand;
import org.naho.speech.llm.command.StartSpeakingTopicCommand;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.out.AiChatPort;
import org.naho.speech.llm.port.out.SessionStorePort;
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
            You are a friendly and patient Japanese conversation partner for language learners.
            Your role:
            - Match the learner's level: if they use simple Japanese, respond simply.
            - Keep your responses concise (2-4 sentences max) to encourage the learner to speak more.
            - Ask follow-up questions to keep the conversation going.
            %s
            
            IMPORTANT OUTPUT FORMAT REQUIREMENT:
            You MUST always respond with ONLY a valid, raw JSON object (no markdown formatting, no code blocks like ```json).
            The JSON object MUST contain the following 5 string fields:
            {
              "reply": "Your conversation response ONLY in Japanese (日本語のみ), matching persona & level",
              "replyTranslation": "Dịch nghĩa tiếng Việt câu trả lời 'reply' của bạn",
              "grammarExplanation": "Giải thích cấu trúc ngữ pháp hoặc từ vựng chính trong câu 'reply' của bạn bằng tiếng Việt",
              "correctedUserText": "Câu tiếng Nhật đã được sửa lỗi ngữ pháp/từ vựng/kính ngữ cho lượt vừa rồi của học viên (nếu câu của học viên đã đúng hoàn toàn thì giữ nguyên hoặc đưa ra cách diễn đạt tự nhiên hơn)",
              "correctionExplanation": "Giải thích chi tiết lỗi sai và lý do sửa/cải thiện bằng tiếng Việt (nếu câu của học viên không có lỗi thì ghi 'Câu của bạn đã chính xác và tự nhiên!')"
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

    public SpeakingSessionUseCase(AiChatPort aiChatPort,
                                  SessionStorePort sessionStorePort,
                                  SpeechToTextPort speechToTextPort,
                                  PersonaRepositoryPort personaRepositoryPort,
                                  TextToSpeechServicePort textToSpeechServicePort) {
        this.aiChatPort = aiChatPort;
        this.sessionStorePort = sessionStorePort;
        this.speechToTextPort = speechToTextPort;
        this.personaRepositoryPort = personaRepositoryPort;
        this.textToSpeechServicePort = textToSpeechServicePort;
    }

    private record ParsedAiReply(
            String reply,
            String replyTranslation,
            String grammarExplanation,
            String correctedUserText,
            String correctionExplanation
    ) {}

    private ParsedAiReply parseAiResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isBlank()) {
            return new ParsedAiReply("", "", "", "", "");
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
            String grammarExplanation = root.path("grammarExplanation").asText("");
            String correctedUserText = root.path("correctedUserText").asText("");
            String correctionExplanation = root.path("correctionExplanation").asText("");
            return new ParsedAiReply(reply, replyTranslation, grammarExplanation, correctedUserText, correctionExplanation);
        } catch (Exception e) {
            System.out.println("[SpeakingSessionUseCase] Fallback raw text parsing: " + e.getMessage());
            return new ParsedAiReply(rawResponse, "", "", "", "");
        }
    }

    @Override
    public SpeakingTopicResult startTopicSession(StartSpeakingTopicCommand command) {
        String sessionId = UUID.randomUUID().toString();
        String topic = command.topic();
        sessionStorePort.initSession(sessionId);
        sessionStorePort.setTopic(sessionId, topic);
        sessionStorePort.setVoiceName(sessionId, "ja-JP-NanamiNeural");

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

        String audioBase64 = toAudioBase64(sessionId, parsed.reply());
        System.out.println("[SpeakingSession] Topic session started: " + sessionId + " | Topic: " + topic);

        return new SpeakingTopicResult(
                sessionId,
                topic,
                parsed.reply(),
                audioBase64,
                parsed.replyTranslation(),
                parsed.grammarExplanation()
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

        String aiAudio = toAudioBase64(sessionId, parsed.reply());

        return new ChatResult(
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarExplanation(),
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
            if (persona.getConversationStyle().getFormalityLevel() != null) {
                personaCtx.append("formalityLevel: ").append(persona.getConversationStyle().getFormalityLevel().name()).append("\n");
            }
            if (persona.getConversationStyle().getMarugotoLevel() != null) {
                personaCtx.append("marugotoLevel: ").append(persona.getConversationStyle().getMarugotoLevel().name()).append("\n");
            }
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
            if (persona.getConversationStyle().getFormalityLevel() != null) {
                customInstruction.append("\n- Formality level (Keigo/Style): ").append(persona.getConversationStyle().getFormalityLevel().name());
            }
            if (persona.getConversationStyle().getMarugotoLevel() != null) {
                customInstruction.append("\n- Marugoto course level: ").append(persona.getConversationStyle().getMarugotoLevel().name());
            }
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

        String audioBase64 = toAudioBase64(sessionId, parsed.reply());

        return new StartConversationResult(
                sessionId,
                audioBase64,
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarExplanation()
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

        String aiAudio = toAudioBase64(sessionId, parsed.reply());

        return new AudioChatResult(
                transcribedText,
                parsed.reply(),
                parsed.replyTranslation(),
                parsed.grammarExplanation(),
                parsed.correctedUserText(),
                parsed.correctionExplanation(),
                aiAudio,
                sttResult.accuracyScore(),
                sttResult.fluencyScore(),
                sttResult.completenessScore(),
                sttResult.pronunciationScore()
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
}
