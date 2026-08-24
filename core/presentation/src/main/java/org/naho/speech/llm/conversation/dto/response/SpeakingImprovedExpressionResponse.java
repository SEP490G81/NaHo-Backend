package org.naho.speech.llm.conversation.dto.response;

public record SpeakingImprovedExpressionResponse(
        Long id,
        Long speakingSessionAssessmentId,
        String originalText,
        String improvedText,
        String explanationVietnamese
) {
}
