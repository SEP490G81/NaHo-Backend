package org.naho.question.port.out;

import org.naho.question.model.Grammar;
import java.util.List;

public interface SaveGrammarPort {
    void saveAll(List<Grammar> grammars);
}
