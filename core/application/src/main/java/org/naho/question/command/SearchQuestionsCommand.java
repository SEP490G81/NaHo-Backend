package org.naho.question.command;

import org.naho.question.type.QuestionStatus;

import java.util.List;

public record SearchQuestionsCommand(
        int page,
        int size,
        Long objectiveId,
        Long creatorId,
        Boolean isSystemCreated,
        List<QuestionStatus> statuses,
        String keyword,
        String sortBy,
        String sortDirection
) {
}

