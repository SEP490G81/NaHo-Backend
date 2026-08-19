package org.naho.vocabulary.port.in;

import java.io.ByteArrayInputStream;

public interface ExportVocabularyInputPort {
    ByteArrayInputStream exportByQuestion(Long questionId);

    ByteArrayInputStream exportByObjective(Long objectiveId);
}
