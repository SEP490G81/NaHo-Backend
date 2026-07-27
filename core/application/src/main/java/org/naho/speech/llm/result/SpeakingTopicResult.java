package org.naho.speech.llm.result;

public record SpeakingTopicResult(
        String sessionId,
        String topic,
        String aiGreeting,
        String audioBase64,
        String aiGreetingTranslation,
        String grammarExplanation
) {
}
