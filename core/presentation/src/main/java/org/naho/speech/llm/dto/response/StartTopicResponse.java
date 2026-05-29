package org.naho.speech.llm.dto.response;

/**
 * HTTP Response DTO: Kết quả bắt đầu topic session.
 */
public record StartTopicResponse(
        String sessionId,
        String topic,
        String aiGreeting
) {
}
