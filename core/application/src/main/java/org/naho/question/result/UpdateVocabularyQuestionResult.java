package org.naho.question.result;

import org.naho.vocabulary.result.VocabularyResult;

import java.util.List;

public record UpdateVocabularyQuestionResult(
        Long id,
        List<VocabularyResult> vocabularies
) {
}
