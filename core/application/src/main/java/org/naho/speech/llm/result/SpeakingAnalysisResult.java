package org.naho.speech.llm.result;

public record SpeakingAnalysisResult(
        Long historyId,
        Double score,
        String audioUrl
) {
}
