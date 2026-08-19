package org.naho.grammar.port.in;

import org.naho.grammar.result.GrammarResult;

public interface GetGrammarDetailInputPort {
    GrammarResult getGrammarDetail(Long id);
}
