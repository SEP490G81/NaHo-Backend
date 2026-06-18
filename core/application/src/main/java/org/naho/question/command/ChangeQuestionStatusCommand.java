package org.naho.question.command;

import org.naho.question.type.QuestionStatus;

public record ChangeQuestionStatusCommand(
        Long questionId,
        Long userId,
        QuestionStatus newStatus,
        String rejectReason,
        boolean isAdminOrManager
) {
}

