package org.naho.vocabulary.port.out;

import org.naho.vocabulary.model.Vocabulary;

import java.io.InputStream;
import java.util.List;

public interface VocabularyExcelParserPort {
    List<Vocabulary> parseVocabularyExcel(InputStream inputStream);
}
