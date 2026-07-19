package org.naho.vocabulary.result;

import java.util.List;

public record VocabularyQuestionDetailResult(
        Long id,
        List<VocabularyDetailResult> vocabularies
) {
}
