package org.naho.vocabulary.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.vocabulary.entity.VocabularyEntity;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface VocabularyJpaRepository extends BaseJpaRepository<VocabularyEntity> {
    List<VocabularyEntity> findAllByIdIn(Collection<Long> ids);
}
