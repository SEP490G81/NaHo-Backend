package org.naho.speech.llm.result;

public record AudioChatResult(
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
