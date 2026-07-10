package org.naho.question.dto.response;

import org.naho.question.type.QuestionStatus;

import java.time.ZonedDateTime;

public record SpeakingQuestionListItemResponse(
        Long id,
        Long userId,
        String titleMarkup,
        String descriptionMarkup,
        QuestionStatus status,
        ZonedDateTime createdTime
) {
}
