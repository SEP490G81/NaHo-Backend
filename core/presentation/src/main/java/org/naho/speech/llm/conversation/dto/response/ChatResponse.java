package org.naho.speech.llm.conversation.dto.response;

public record ChatResponse(
        String assistantReply,
        String assistantReplyTranslation,
        String grammarExplanation,
        String correctedUserText,
        String correctionExplanation,
        String aiReplyAudio
) {
}
