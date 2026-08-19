package org.naho.vocabulary.result;

import java.util.List;

public record VocabularyQuestionResult(
        Long id,
        List<VocabularyResult> vocabularies
) {
}
