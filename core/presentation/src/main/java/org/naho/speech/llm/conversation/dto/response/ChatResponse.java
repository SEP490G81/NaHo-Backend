package org.naho.speech.llm.conversation.dto.response;

public record ChatResponse(
        SpeakingSessionMessageResponse userMessage,
        SpeakingSessionMessageResponse aiMessage
) {
}
