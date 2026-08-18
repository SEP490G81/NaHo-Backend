package org.naho.speech.llm.conversation.result;

import org.naho.speech.azure.result.WordAssessmentResult;

import java.util.List;

public record SpeakingAnalysisReportResult(
        Double overallScore,
        Scores scores,
        String fullTranscript,
        List<WordAssessmentResult> wordPronunciations,
        String enrichedFeedbackJson
) {
    public List<WordAssessmentResult> wordPronunciationResultList() {
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

