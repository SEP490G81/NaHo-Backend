package org.naho.vocabulary.port.out;

import org.naho.vocabulary.model.Vocabulary;

import java.util.List;

public interface VocabularyPort {
    List<Vocabulary> findVocabularyList(int vocabularyQuestionId);

}
