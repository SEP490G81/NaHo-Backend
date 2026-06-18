package org.naho.question.dto.response;

import org.naho.question.type.QuestionStatus;

import java.time.ZonedDateTime;

public record QuestionListItemResponse(
        Long id,
        Long topicId,
        Long userId,
        String titleMarkup,
        String descriptionMarkup,
        Double orderIndex,
        QuestionStatus status,
        ZonedDateTime createdTime
) {
}
