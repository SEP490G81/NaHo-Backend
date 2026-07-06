package org.naho.question.dto.response;

import org.naho.question.type.QuestionStatus;

public record QuestionDetailResponse(
        Long id,
        Long objectiveId,
        Long userId,
        String title,
        String titleMarkup,
        String description,
        String descriptionMarkup,
        Double orderIndex,
        QuestionStatus status
) {
}
