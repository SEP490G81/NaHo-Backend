package org.naho.vocabulary.port.in;

import java.io.ByteArrayInputStream;

public interface ExportVocabularyInputPort {
    ByteArrayInputStream exportByQuestion(int questionId);
    ByteArrayInputStream exportByObjective(int objectiveId);
}
