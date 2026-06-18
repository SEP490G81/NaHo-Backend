package org.naho.question.dto.request;

import jakarta.validation.constraints.NotNull;
import org.naho.question.type.QuestionStatus;

public record ChangeQuestionStatusRequest(
        @NotNull(message = "New status is required")
        QuestionStatus newStatus,

        String rejectReason
) {
}
