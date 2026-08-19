package org.naho.vocabulary.port.in;

import org.naho.vocabulary.result.VocabulariesOfObjectiveResult;

public interface GetVocabulariesOfObjectiveInputPort {
    VocabulariesOfObjectiveResult getVocabularyListOfObjective(Long objectiveId);
}
