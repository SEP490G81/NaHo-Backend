package org.naho.speech.llm.internal;

public record ParsedScores(
        double vocabScore,
        double grammarScore,
        double naturalnessScore,
        double overallScore,
        String enrichedFeedbackJson
) {
}
