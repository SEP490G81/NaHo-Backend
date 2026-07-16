package org.naho.speech.llm.dto.response;

public record StartConversationResponse(
        String sessionId,
        String audioBase64,
        String aiGreeting
) {
}
