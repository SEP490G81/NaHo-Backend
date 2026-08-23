package org.naho.speech.llm.conversation.dto.response;

public record SpeakingSessionAssessmentResponse(
        Long id,
        Long speakingSessionId,

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
        String studyReason,
        String studyRecommendation,
        String studyEncouragement
) {
}
