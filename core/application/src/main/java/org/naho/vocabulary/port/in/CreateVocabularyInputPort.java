package org.naho.vocabulary.port.in;

import org.naho.vocabulary.command.CreateVocabularyCommand;
import org.naho.vocabulary.result.VocabularyResult;

public interface CreateVocabularyInputPort {
    VocabularyResult createVocabulary(CreateVocabularyCommand command);
}
