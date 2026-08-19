package org.naho.grammar.port.in;

import org.naho.grammar.command.CreateGrammarCommand;
import org.naho.grammar.result.GrammarResult;

public interface CreateGrammarInputPort {
    GrammarResult createGrammar(CreateGrammarCommand command);
}
