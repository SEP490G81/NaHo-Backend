package org.naho.grammar.command;

public record UpdateGrammarCommand(
        Long id,
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
