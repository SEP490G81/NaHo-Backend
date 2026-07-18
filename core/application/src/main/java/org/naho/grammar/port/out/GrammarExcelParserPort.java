package org.naho.grammar.port.out;

import org.naho.question.model.Grammar;
import java.io.InputStream;
import java.util.List;

public interface GrammarExcelParserPort {
    List<Grammar> parseGrammarExcel(InputStream inputStream);
}
