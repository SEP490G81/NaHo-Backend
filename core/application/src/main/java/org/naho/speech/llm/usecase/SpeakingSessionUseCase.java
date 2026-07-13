package org.naho.speech.llm.usecase;

import org.naho.speech.llm.command.SendAudioMessageCommand;
import org.naho.speech.llm.command.SendMessageWithSessionCommand;
import org.naho.speech.llm.command.StartSpeakingCommand;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.out.AiChatPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeechToTextPort;
import org.naho.speech.llm.result.AudioChatResult;
import org.naho.speech.llm.result.ChatResult;
import org.naho.speech.llm.result.SpeakingTopicResult;
import org.naho.speech.llm.result.SpeechToTextResult;
import org.naho.persona.port.out.PersonaRepositoryPort;
import org.naho.persona.model.Persona;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SpeakingSessionUseCase implements SpeakingSessionInputPort {
    private static final String SYSTEM_PROMPT_TEMPLATE = """
            You are a friendly and patient Japanese conversation partner for language learners.
            Your role:
            - Always respond ONLY in Japanese (日本語のみ).
            - Match the learner's level: if they use simple Japanese, respond simply.
            - If the learner makes mistakes, gently continue the conversation naturally \
            (do NOT correct grammar explicitly during the conversation).
            - Keep your responses concise (2-4 sentences max) to encourage the learner to speak more.
            - Ask follow-up questions to keep the conversation going.
            %s""";

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

    public SpeakingSessionUseCase(AiChatPort aiChatPort,
                                  SessionStorePort sessionStorePort,
                                  SpeechToTextPort speechToTextPort,
                                  PersonaRepositoryPort personaRepositoryPort) {
        this.aiChatPort = aiChatPort;
        this.sessionStorePort = sessionStorePort;
        this.speechToTextPort = speechToTextPort;
        this.personaRepositoryPort = personaRepositoryPort;
    }


    @Override
    public SpeakingTopicResult startTopicSession(StartSpeakingCommand command) {
        String sessionId = UUID.randomUUID().toString();
        String topic = command.topic();
        sessionStorePort.initSession(sessionId);
        sessionStorePort.setTopic(sessionId, topic);
        String prompt = SYSTEM_PROMPT_TEMPLATE.formatted(TOPIC_INSTRUCTION.formatted(topic));
        sessionStorePort.addMessage(sessionId, "system", prompt);
        sessionStorePort.addMessage(sessionId, "user", "こんにちは、話しましょう！");
        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionId);
        String aiGreeting = aiChatPort.chatWithContext(messages);
        sessionStorePort.addMessage(sessionId, "assistant", aiGreeting);
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: こんにちは、話しましょう！\nAssistant: " + aiGreeting + "\n"
        );
        System.out.println("[SpeakingSession] Topic session started: " + sessionId
                + " | Topic: " + topic);
        return new SpeakingTopicResult(sessionId, topic, aiGreeting);
    }

    @Override
    public String startFreeSession(Long personaId) {
        String sessionId = UUID.randomUUID().toString();
        sessionStorePort.initSession(sessionId);
        sessionStorePort.setTopic(sessionId, "Free conversation");

        String customInstruction = FREE_INSTRUCTION;
        if (personaId != null) {
            Persona persona = personaRepositoryPort.findById(personaId)
                    .orElseThrow(() -> new IllegalArgumentException("Persona with ID " + personaId + " not found"));
            customInstruction += "\n- Your persona prompt: " + persona.getPrompt();
        }

        String prompt = SYSTEM_PROMPT_TEMPLATE.formatted(customInstruction);
        sessionStorePort.addMessage(sessionId, "system", prompt);
        System.out.println("Free session started: " + sessionId + (personaId != null ? " with Persona: " + personaId : ""));
        return sessionId;
    }

    @Override
    public ChatResult sendMessage(SendMessageWithSessionCommand command) {
        String sessionId = command.sessionId();
        String userMessage = command.userMessage();
        sessionStorePort.addMessage(sessionId, "user", userMessage);
        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionId);
        String reply = aiChatPort.chatWithContext(messages);
        sessionStorePort.addMessage(sessionId, "assistant", reply);
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + userMessage + "\nAssistant: " + reply + "\n"
        );
        return new ChatResult(reply);
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
                }
        );
        String reply = fullReply.toString();
        sessionStorePort.addMessage(sessionId, "assistant", reply);
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + userMessage + "\nAssistant: " + reply + "\n");
    }

    /**
     * Luồng hoàn chỉnh cho audio message:
     * 1. Gọi SpeechToTextPort → Azure STT + Pronunciation Assessment → transcript + scores
     * 2. Thêm user message (transcript) vào conversation history
     * 3. Gọi AiChatPort → AI reply dựa trên context
     * 4. Lưu assistant reply + append transcript
     * 5. Trả về AudioChatResult (transcript + AI reply + pronunciation scores)
     */
    @Override
    public AudioChatResult sendAudioMessage(SendAudioMessageCommand command) {
        String sessionId = command.sessionId();

        // 1. Speech-to-Text + Pronunciation Assessment
        SpeechToTextResult sttResult = speechToTextPort.transcribeAndAssess(
                command.audioBytes(), command.referenceText());

        String transcribedText = sttResult.transcribedText();
        System.out.println("[SpeakingSession] STT result: " + transcribedText);

        // 2. Thêm user message vào conversation history
        sessionStorePort.addMessage(sessionId, "user", transcribedText);

        // 3. Gọi AI reply
        List<Map<String, String>> messages = sessionStorePort.getConversationHistory(sessionId);
        String aiReply = aiChatPort.chatWithContext(messages);

        // 4. Lưu assistant reply
        sessionStorePort.addMessage(sessionId, "assistant", aiReply);
        sessionStorePort.appendTranscript(sessionId,
                "[Turn]\nUser: " + transcribedText + "\nAssistant: " + aiReply + "\n");

        // 5. Trả về kết quả tổng hợp
        return new AudioChatResult(
                transcribedText,
                aiReply,
                sttResult.accuracyScore(),
                sttResult.fluencyScore(),
                sttResult.completenessScore(),
                sttResult.pronunciationScore()
        );
    }
}
