package org.naho.speech.llm.result;

import java.util.List;

public record SpeakingAnalysisReportResult(
        Double overallScore,
        Scores scores,
        List<WordPronunciationResult> wordPronunciations,
        String enrichedFeedbackJson
) {
    public List<WordPronunciationResult> wordPronunciationResultList() {
        return wordPronunciations;
    }

    public record Scores(
            Double pronunciation,
            Double fluency,
            Double vocabulary,
            Double grammar,
            Double naturalness
    ) {
    }
}

