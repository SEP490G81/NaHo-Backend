package org.naho.speech.llm.dto.response;

import java.util.List;

public record SpeakingAnalysisReportResponse(
        Double overallScore,
        Scores scores,
        List<WordPronunciationResponse> wordPronunciations,
        String enrichedFeedbackJson
) {
    public record Scores(
            Double pronunciation,
            Double fluency,
            Double vocabulary,
            Double grammar,
            Double naturalness
    ) {
    }
}
