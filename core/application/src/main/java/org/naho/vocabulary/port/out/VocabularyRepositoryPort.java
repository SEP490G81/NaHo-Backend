package org.naho.vocabulary.port.out;

import org.naho.pagination.PageData;
import org.naho.vocabulary.model.Vocabulary;

import java.util.Optional;

public interface VocabularyRepositoryPort {
    Vocabulary save(Vocabulary vocabulary);

    Optional<Vocabulary> findById(Long id);

    void deleteById(Long id);

    PageData<Vocabulary> searchByKeyword(String keyword, int page, int size);
}
