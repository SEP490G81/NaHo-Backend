package org.naho.grammar.mapper;

import org.naho.grammar.result.GrammarResult;
import org.naho.question.model.Grammar;

public class GrammarResultMapper {
    public GrammarResult domainToResult(Grammar grammar) {
        if (grammar == null) {
            return null;
        }
        return GrammarResult.builder()
                .id(grammar.getId())
                .reading(grammar.getReading())
                .japanese(grammar.getJapanese())
                .vietnameseMeaningText(grammar.getVietnameseMeaningText())
                .englishMeaningText(grammar.getEnglishMeaningText())
                .build();
    }
}
