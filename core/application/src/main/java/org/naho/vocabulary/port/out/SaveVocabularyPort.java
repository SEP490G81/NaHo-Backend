package org.naho.vocabulary.port.out;

import org.naho.question.model.Vocabulary;

import java.util.List;

public interface SaveVocabularyPort {
    void saveAll(List<Vocabulary> vocabularies);
}
