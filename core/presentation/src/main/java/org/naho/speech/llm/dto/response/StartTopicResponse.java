package org.naho.speech.llm.dto.response;

public record StartTopicResponse(
        String sessionId,
        String topic,
        String aiGreeting,
        String audioBase64,
        String aiGreetingTranslation,
        String grammarExplanation
) {
}
