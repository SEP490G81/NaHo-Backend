package org.naho.speech.llm.conversation.result;

public record SpeakingImprovedExpressionResult(
        Long id,
        Long speakingSessionAssessmentId,
        String originalText,
        String improvedText,
        String explanationVietnamese
) {
}
