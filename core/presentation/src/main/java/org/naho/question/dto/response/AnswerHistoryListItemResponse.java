package org.naho.question.dto.response;

import org.naho.file.dto.response.FileResponse;

public record AnswerHistoryListItemResponse(
        Long id,
        Long userId,
        SpeakingQuestionListItemResponse speakingQuestion,
        FileResponse audioFile,
        Double duration,
        Double overallScore
) {
}
