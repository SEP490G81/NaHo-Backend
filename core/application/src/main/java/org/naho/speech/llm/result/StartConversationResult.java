package org.naho.speech.llm.result;

public record StartConversationResult(
        String sessionCode,
        String audioBase64,
        String aiGreeting,
        String aiGreetingTranslation,
        String grammarExplanation
) {
}
