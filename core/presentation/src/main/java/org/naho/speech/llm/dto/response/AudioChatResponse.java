package org.naho.speech.llm.dto.response;

public record AudioChatResponse(
        String transcribedText,
        String assistantReply,
        String assistantReplyTranslation,
        String grammarExplanation,
        String correctedUserText,
        String correctionExplanation,
        String aiReplyAudio,
        Double accuracyScore,
        Double fluencyScore,
        Double completenessScore,
        Double pronunciationScore
) {
}
