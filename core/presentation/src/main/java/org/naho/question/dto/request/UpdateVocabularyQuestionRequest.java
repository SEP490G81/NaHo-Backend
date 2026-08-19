package org.naho.question.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateVocabularyQuestionRequest(
        @NotNull(message = "Vocabularies list cannot be null")
        List<NestedVocabularyRequest> vocabularies
) {
    public record NestedVocabularyRequest(
            Long id,
            String reading,
            String japanese,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {
    }
}
