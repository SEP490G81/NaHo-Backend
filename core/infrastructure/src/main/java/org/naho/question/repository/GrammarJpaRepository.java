package org.naho.question.repository;

import org.naho.grammar.entity.GrammarEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.Collection;
import java.util.List;

public interface GrammarJpaRepository extends BaseJpaRepository<GrammarEntity> {
    List<GrammarEntity> findAllByIdIn(Collection<Long> ids);
}
