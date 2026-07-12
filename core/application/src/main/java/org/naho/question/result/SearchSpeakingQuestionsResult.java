package org.naho.question.result;

import java.util.List;

public record SearchSpeakingQuestionsResult(
        List<SpeakingQuestionListItemResult> items,
        int page,
        int size,
        int totalPages,
        long totalElements
) {
}
