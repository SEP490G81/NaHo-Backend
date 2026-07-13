package org.naho.vocabulary.port.out;

import org.naho.vocabulary.model.Vocabulary;
import java.io.ByteArrayInputStream;
import java.util.List;

public interface ExcelWriterPort {
    ByteArrayInputStream writeVocabulariesToExcel(List<Vocabulary> vocabularies);
}
