package org.naho.question.result;

import java.util.List;

public record SearchQuestionsResult(
        List<QuestionListItemResult> items,
        int page,
        int size,
        int totalPages,
        long totalElements
) {
}
