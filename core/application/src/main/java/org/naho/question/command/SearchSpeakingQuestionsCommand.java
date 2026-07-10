package org.naho.question.command;

import org.naho.question.type.QuestionStatus;

import java.util.List;

public record SearchSpeakingQuestionsCommand(
        int page,
        int size,
        Long creatorId,
        Boolean isSystemCreated,
        List<QuestionStatus> statuses,
        String keyword,
        String sortBy,
        String sortDirection
) {
}
