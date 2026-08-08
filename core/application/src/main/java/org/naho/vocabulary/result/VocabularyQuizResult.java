package org.naho.vocabulary.result;

import java.util.List;

public record VocabularyQuizResult(
        Long vocabularyId,
        String japanese,
        String reading,
        List<QuizOptionResult> options,
        String correctOptionId
) {
    public record QuizOptionResult(
            String id,
            String text
    ) {
    }
}
