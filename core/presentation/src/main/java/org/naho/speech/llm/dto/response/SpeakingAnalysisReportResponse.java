package org.naho.speech.llm.dto.response;

import org.naho.speech.azure.dto.response.WordAssessmentResponse;

import java.util.List;

public record SpeakingAnalysisReportResponse(
        Double overallScore,
        Scores scores,
        String fullTranscript,
        List<WordAssessmentResponse> wordPronunciations,
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
