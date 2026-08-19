package org.naho.grammar.command;

public record CreateGrammarCommand(
        String reading,
        String japanese,
        String vietnameseMeaningText,
        String englishMeaningText
) {
}
