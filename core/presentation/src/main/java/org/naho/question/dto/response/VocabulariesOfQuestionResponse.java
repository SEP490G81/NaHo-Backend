package org.naho.question.dto.response;

import java.util.List;

public record VocabulariesOfQuestionResponse(
        int nodeId,
        int vocabularyQuestionId,
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
