package org.naho.vocabulary.result;

import java.util.List;

public record VocabulariesOfObjectiveResult(
        int objectiveId,
        List<VocabularyDetailResult> vocabularyDetailResults
) {
    public record VocabularyDetailResult(
            Long id,
            String kana,
            String kanji,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {
    }
}
