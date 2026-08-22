package org.naho.speech.llm.conversation.dto.request;

/**
 * HTTP Request DTO: Gửi text message trong speaking session.
 */
public record ChatSessionMessageRequest(
        String sessionCode,
        String userMessage
) {
}
