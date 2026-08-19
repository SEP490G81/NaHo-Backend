package org.naho.vocabulary.port.out;

import org.naho.question.model.Vocabulary;

import java.util.List;

public interface VocabularyPort {
    List<Vocabulary> findVocabularyList(Long vocabularyQuestionId);
}
