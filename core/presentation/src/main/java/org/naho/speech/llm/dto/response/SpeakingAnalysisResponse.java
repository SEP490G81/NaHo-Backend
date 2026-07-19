package org.naho.speech.llm.dto.response;

public record SpeakingAnalysisResponse(
        Long historyId,
        Double score
) {
}
