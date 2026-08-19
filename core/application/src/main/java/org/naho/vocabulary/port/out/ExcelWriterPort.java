package org.naho.vocabulary.port.out;

import org.naho.question.model.Vocabulary;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface ExcelWriterPort {
    ByteArrayInputStream writeVocabulariesToExcel(List<Vocabulary> vocabularies);
}
