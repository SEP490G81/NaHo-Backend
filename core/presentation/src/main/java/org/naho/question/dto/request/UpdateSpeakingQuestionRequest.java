package org.naho.question.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateSpeakingQuestionRequest(
        @NotBlank(message = "Japanese name is required")
        String japaneseName,

        String vietnameseName,

        String description,

        String japaneseSampleAnswer,

        String vietnameseSampleAnswer,

        String englishSampleAnswer,

        List<NestedVocabularyRequest> vocabularies,

        List<NestedGrammarRequest> grammars
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
