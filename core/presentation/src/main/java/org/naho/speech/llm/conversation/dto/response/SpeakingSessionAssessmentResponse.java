package org.naho.speech.llm.conversation.dto.response;

import java.util.List;

public record SpeakingSessionAssessmentResponse(
        Long id,
        Long speakingSessionId,

        int overallScore,
        String jlptEstimate,
        int fluencyScore,
        int pronunciationScore,
        int grammarScore,
        int vocabularyScore,
        int interactionScore,
        int naturalnessScore,
        int coherenceScore,

        String summary,
        String strengths,
        String weaknesses,

        String feedbackFluency,
        String feedbackPronunciation,
        String feedbackGrammar,
        String feedbackVocabulary,
        String feedbackInteraction,
        String feedbackNaturalness,
        String feedbackCoherence,

        String studyFocusArea,
        String studyRecommendation,
        String studyEncouragement,

        List<SpeakingImprovedExpressionResponse> speakingImprovedExpressions
) {
}
