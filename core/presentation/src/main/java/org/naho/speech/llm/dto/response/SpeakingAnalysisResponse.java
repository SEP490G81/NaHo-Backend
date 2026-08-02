package org.naho.speech.llm.dto.response;

import org.naho.file.result.FileResult;

public record SpeakingAnalysisResponse(
        Long answerHistoryId,
        Double overallScore,
        FileResult audioFile
) {
}
