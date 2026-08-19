package org.naho.grammar.port.out;

import org.naho.pagination.PageData;
import org.naho.question.model.Grammar;

import java.util.Optional;

public interface GrammarRepositoryPort {
    Grammar save(Grammar grammar);

    Optional<Grammar> findById(Long id);

    void deleteById(Long id);

    PageData<Grammar> searchByKeyword(String keyword, int page, int size);
}
