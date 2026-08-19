package org.naho.vocabulary.port.in;

import org.naho.vocabulary.command.UpdateVocabularyCommand;
import org.naho.vocabulary.result.VocabularyResult;

public interface UpdateVocabularyInputPort {
    VocabularyResult updateVocabulary(UpdateVocabularyCommand command);
}
