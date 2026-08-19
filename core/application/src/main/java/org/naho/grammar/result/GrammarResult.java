package org.naho.grammar.result;

public record GrammarResult(
        Long id,
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
