package org.naho.grammar.command;

public record SearchGrammarCommand(
        String keyword,
        int page,
        int size
) {
}
