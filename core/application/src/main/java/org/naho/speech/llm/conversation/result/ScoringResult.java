package org.naho.speech.llm.conversation.result;

import java.util.List;
import java.util.Map;

public record ScoringResult(
        String sessionCode,
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
        List<String> strengths,
        List<String> weaknesses,
        Map<String, String> feedback,
        List<ImprovedExpression> improvedExpressions,
        StudyRecommendation studyRecommendation
) {
    public record ImprovedExpression(String original, String improved, String explanationVi) {
    }

    public record StudyRecommendation(
            String focusArea,
            String reason,
            String suggestedPractice,
            String encouragement
    ) {
    }
}


