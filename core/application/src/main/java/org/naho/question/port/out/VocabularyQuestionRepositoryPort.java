package org.naho.question.port.out;

import org.naho.question.model.VocabularyQuestion;

import java.util.Optional;

public interface VocabularyQuestionRepositoryPort {
    Optional<VocabularyQuestion> findById(Long id);

    VocabularyQuestion save(VocabularyQuestion vocabularyQuestion);
}
