package org.naho.speech.llm.conversation.dto.response;

import java.util.List;

public record SpeakingSessionAssessmentResponse(
        Long id,
        Long speakingSessionId,

        Integer overallScore,
        String jlptEstimate,
        Integer fluencyScore,
        Integer pronunciationScore,
        Integer grammarScore,
        Integer vocabularyScore,
        Integer interactionScore,
        Integer naturalnessScore,
        Integer coherenceScore,

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
