package org.naho.grammar.usecase;

import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.in.GetGrammarDetailInputPort;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.grammar.result.GrammarResult;
import org.naho.question.model.Grammar;
import org.naho.shared.exception.ApplicationException;

public class GetGrammarDetailUseCase implements GetGrammarDetailInputPort {

    private final GrammarRepositoryPort grammarRepositoryPort;

    public GetGrammarDetailUseCase(GrammarRepositoryPort grammarRepositoryPort) {
        this.grammarRepositoryPort = grammarRepositoryPort;
    }

    @Override
    public GrammarResult getGrammarDetail(Long id) {
        Grammar grammar = grammarRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        GrammarErrorCode.GRAMMAR_NOT_FOUND,
                        "grammar.not.found"
                ));

        return new GrammarResult(
                grammar.getId(),
                grammar.getReading(),
                grammar.getJapanese(),
                grammar.getVietnameseMeaningText(),
                grammar.getEnglishMeaningText()
        );
    }
}
