package org.naho.vocabulary.port.out;

import org.naho.question.model.Vocabulary;

import java.io.InputStream;
import java.util.List;

public interface VocabularyExcelParserPort {
    List<Vocabulary> parseVocabularyExcel(InputStream inputStream);
}
