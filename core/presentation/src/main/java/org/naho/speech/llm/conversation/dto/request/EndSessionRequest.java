package org.naho.speech.llm.conversation.dto.request;

/**
 * HTTP Request DTO: Kết thúc speaking session (metadata cho scoring).
 */
public record EndSessionRequest(
        String topic,
        String speechMetadata,
        String asrConfidence
) {
}
