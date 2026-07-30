package org.naho.speech.llm.result;

public record ChatResult(
        String assistantReply,
        String assistantReplyTranslation,
        String grammarExplanation,
        String correctedUserText,
        String correctionExplanation,
        String aiReplyAudio
) {
}
