package org.naho.speech.llm.result;

import java.util.List;
import java.util.Map;

public record ScoringResult(
        String sessionId,
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
        List<ImprovedExpression> improvedExpressions
) {
    public record ImprovedExpression(String original, String improved) {
    }

}


