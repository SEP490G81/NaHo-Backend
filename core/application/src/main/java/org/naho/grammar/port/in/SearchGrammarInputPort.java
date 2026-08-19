package org.naho.grammar.port.in;

import org.naho.grammar.command.SearchGrammarCommand;
import org.naho.grammar.result.GrammarResult;
import org.naho.pagination.PageData;

public interface SearchGrammarInputPort {
    PageData<GrammarResult> searchGrammars(SearchGrammarCommand command);
}
