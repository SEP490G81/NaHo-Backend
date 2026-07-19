package org.naho.grammar.result;

public record GrammarDetailResult(
        Long id,
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
