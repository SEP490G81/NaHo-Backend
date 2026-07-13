package org.naho.vocabulary.result;

import java.util.List;

public record VocabulariesOfObjectiveResult(
        int objectiveId,
        List<VocabularyDetailResult> vocabularyDetailResults
) {
    public record VocabularyDetailResult(
            Long id,
            String reading,
            String japanese,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {
    }
}
