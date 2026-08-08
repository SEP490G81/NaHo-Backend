package org.naho.speech.llm.dto.response;

import org.naho.file.dto.response.FileResponse;

public record SpeakingAnalysisResponse(
        Long answerHistoryId,
        Double overallScore,
        FileResponse audioFile,
        SpeakingAnalysisReportResponse report
) {}
