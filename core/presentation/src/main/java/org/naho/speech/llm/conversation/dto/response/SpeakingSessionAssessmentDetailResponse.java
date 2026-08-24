package org.naho.speech.llm.conversation.dto.response;

import org.naho.persona.dto.response.PersonaResponse;

public record SpeakingSessionAssessmentDetailResponse(
        Long id,
        Long speakingSessionId,

        PersonaResponse persona,

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
