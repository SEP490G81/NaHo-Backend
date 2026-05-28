package org.naho.ai.result;

public record SpeakingTopicResult(
        String sessionId,
        String topic,
        String aiGreeting
) {
}
