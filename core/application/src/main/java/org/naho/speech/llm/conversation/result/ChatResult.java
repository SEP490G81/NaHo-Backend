package org.naho.speech.llm.conversation.result;

public record ChatResult(
        SpeakingSessionMessageResult userMessage,
        SpeakingSessionMessageResult aiMessage
) {
}
