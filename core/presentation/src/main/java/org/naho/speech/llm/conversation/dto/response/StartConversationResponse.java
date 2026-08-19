package org.naho.speech.llm.conversation.dto.response;

public record StartConversationResponse(
        String sessionCode,
        String audioBase64,
        String content,
        String contentTranslation,
        String grammarNote
) {
}
