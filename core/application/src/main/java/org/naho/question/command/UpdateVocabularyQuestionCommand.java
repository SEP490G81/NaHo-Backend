package org.naho.question.command;

import java.util.List;

public record UpdateVocabularyQuestionCommand(
        Long id,
        List<NestedVocabularyCommand> vocabularies
) {
    public record NestedVocabularyCommand(
            Long id,
            String reading,
            String japanese,
            String vietnameseMeaningText,
            String englishMeaningText
    ) {}
}
