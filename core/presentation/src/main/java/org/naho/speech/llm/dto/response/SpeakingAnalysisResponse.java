package org.naho.speech.llm.dto.response;

public record SpeakingAnalysisResponse(
        String historyId,
        Double score
) {
}
