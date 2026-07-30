package org.naho.speech.llm.result;

public record StartConversationResult(
        String sessionId,
        String audioBase64,
        String aiGreeting,
        String aiGreetingTranslation,
        String grammarExplanation
) {
}
