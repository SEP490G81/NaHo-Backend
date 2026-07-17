package org.naho.learning.result;

import java.util.List;

public record VocabularyQuestionDetailResult(
        Long id,
        List<VocabularyDetailResult> vocabularies
) {
}
