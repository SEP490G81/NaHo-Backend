package org.naho.vocabulary.repository;

import org.naho.vocabulary.entity.VocabularyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface VocabularyJpaRepository extends JpaRepository<VocabularyEntity, Long> {
    List<VocabularyEntity> findAllByIdIn(Collection<Long> ids);
}
