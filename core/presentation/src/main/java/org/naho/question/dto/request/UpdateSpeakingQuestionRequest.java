package org.naho.question.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSpeakingQuestionRequest(
        @NotBlank(message = "Japanese name is required")
        String japaneseName,

        String vietnameseName,

        @NotBlank(message = "Description is required")
        String description,

        String japaneseSampleAnswer,

        String vietnameseSampleAnswer,

        String englishSampleAnswer,

        java.util.List<NestedVocabularyRequest> vocabularies,

        java.util.List<NestedGrammarRequest> grammars
) {
    public record NestedVocabularyRequest(
            Long id,
            String reading,
            String japanese,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {
    }

    public record NestedGrammarRequest(
            Long id,
            String reading,
            String japanese,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {
    }
}
