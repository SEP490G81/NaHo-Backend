package org.naho.question.command;

public record UpdateSpeakingQuestionCommand(
        Long id,
        Long userId,
        String japaneseName,
        String vietnameseName,
        String description,
        String japaneseSampleAnswer,
        String vietnameseSampleAnswer,
        String englishSampleAnswer,
        boolean isContentManager,
        java.util.List<NestedVocabularyCommand> vocabularies,
        java.util.List<NestedGrammarCommand> grammars
) {
    public record NestedVocabularyCommand(
            Long id,
            String reading,
            String japanese,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {
    }

    public record NestedGrammarCommand(
            Long id,
            String reading,
            String japanese,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {
    }
}
