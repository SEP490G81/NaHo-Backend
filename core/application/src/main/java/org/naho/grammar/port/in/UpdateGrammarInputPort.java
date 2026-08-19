package org.naho.grammar.port.in;

import org.naho.grammar.command.UpdateGrammarCommand;
import org.naho.grammar.result.GrammarResult;

public interface UpdateGrammarInputPort {
    GrammarResult updateGrammar(UpdateGrammarCommand command);
}
