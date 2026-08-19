package org.naho.speech.llm.dto.response;

import java.util.List;
import java.util.Map;

/**
 * HTTP Response DTO: Kết quả chấm điểm speaking session.
 */
public record ScoringResponse(
        String sessionCode,
        int overallScore,
        String jlptEstimate,
        Scores scores,
        String summary,
        List<String> strengths,
        List<String> weaknesses,
        Map<String, String> feedback,
        List<ImprovedExpression> improvedExpressions
) {
    public record Scores(
            int fluency,
            int pronunciation,
            int grammar,
            int vocabulary,
            int interaction,
            int naturalness,
            int coherence
    ) {
    }

    public record ImprovedExpression(String original, String improved) {
    }
}
