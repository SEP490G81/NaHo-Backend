package org.naho.vocabulary.dto.response;

import java.util.List;

public record VocabulariesOfObjectiveResponse(
        int objectiveId,
        List<VocabularyDetailResponse> vocabularyDetailResults
) {
    public record VocabularyDetailResponse(
            Long id,
            String reading,
            String japanese,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {
    }
}
