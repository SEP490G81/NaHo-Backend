package org.naho.grammar.usecase;

import org.naho.grammar.exception.GrammarErrorCode;
import org.naho.grammar.port.in.DeleteGrammarInputPort;
import org.naho.grammar.port.out.GrammarRepositoryPort;
import org.naho.shared.exception.ApplicationException;

public class DeleteGrammarUseCase implements DeleteGrammarInputPort {

    private final GrammarRepositoryPort grammarRepositoryPort;

    public DeleteGrammarUseCase(GrammarRepositoryPort grammarRepositoryPort) {
        this.grammarRepositoryPort = grammarRepositoryPort;
    }

    @Override
    public void deleteGrammar(Long id) {
        grammarRepositoryPort.findById(id)
                .orElseThrow(() -> new ApplicationException(
                        GrammarErrorCode.GRAMMAR_NOT_FOUND,
                        "grammar.not.found"
                ));

        grammarRepositoryPort.deleteById(id);
    }
}
