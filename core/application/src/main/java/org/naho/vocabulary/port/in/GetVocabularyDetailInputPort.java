package org.naho.vocabulary.port.in;

import org.naho.vocabulary.result.VocabularyResult;

public interface GetVocabularyDetailInputPort {
    VocabularyResult getVocabularyDetail(Long id);
}
