package org.naho.speech.llm.conversation.internal;

public record ParsedScores(
        double vocabScore,
        double grammarScore,
        double naturalnessScore,
        double overallScore,
        String enrichedFeedbackJson
) {
}
