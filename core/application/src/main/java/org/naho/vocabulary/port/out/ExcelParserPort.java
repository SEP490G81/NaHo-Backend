package org.naho.vocabulary.port.out;

import org.naho.vocabulary.model.VocabularyGrammarImportResult;
import java.io.InputStream;

public interface ExcelParserPort {
    VocabularyGrammarImportResult parseExcel(InputStream inputStream);
}
