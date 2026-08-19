package org.naho.vocabulary.port.out;

import org.naho.question.model.Vocabulary;

import java.util.List;
import java.util.Optional;

public interface RandomVocabularyPort {
    Optional<Vocabulary> findRandomVocabulary();

    List<String> findRandomDistractorMeanings(Long targetId, String targetMeaning, int limit);
}
