package org.naho.speech.llm.conversation.result;

public record SpeakingImprovedExpressionResult(
        Long id,
        Long speakingSessionAssessmentId,
        Integer turnIndex,
        String originalText,
        String improvedText,
        String explanationVietnamese
) {
}
