package org.naho.question.dto.request;

import org.naho.question.type.QuestionStatus;

import java.util.List;

public record QuestionFilterRequest(
        Long objectiveId,
        Long creatorId,
        Boolean isSystemCreated,
        List<QuestionStatus> statuses,
        String keyword
) {
}
