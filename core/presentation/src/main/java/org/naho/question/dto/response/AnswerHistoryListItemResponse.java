package org.naho.question.dto.response;

import org.naho.file.dto.response.FileResponse;

import java.time.Instant;

public record AnswerHistoryListItemResponse(
        Long id,
        Long userId,
        SpeakingQuestionListItemResponse speakingQuestion,
        FileResponse audioFile,
        Double duration,
        Double overallScore,
        Instant createdTime,
        Instant modifiedTime
) {
}
