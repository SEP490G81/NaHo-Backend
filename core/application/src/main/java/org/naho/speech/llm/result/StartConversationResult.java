package org.naho.speech.llm.result;

public record StartConversationResult(
        String sessionCode,
        String audioBase64,
        String content,
        String contentTranslation,
        String grammarNote
) {
}
