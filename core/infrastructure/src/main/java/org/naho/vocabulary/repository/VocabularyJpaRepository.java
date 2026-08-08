package org.naho.vocabulary.repository;

import org.naho.shared.persistence.BaseJpaRepository;
import org.naho.vocabulary.entity.VocabularyEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface VocabularyJpaRepository extends BaseJpaRepository<VocabularyEntity> {

    List<VocabularyEntity> findAllByIdIn(Collection<Long> ids);

    @Query(value = "SELECT * FROM vocabularies WHERE vietnamese_meaning_text IS NOT NULL AND vietnamese_meaning_text != '' ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<VocabularyEntity> findRandomVocabulary();

    @Query(value = "SELECT DISTINCT vietnamese_meaning_text FROM vocabularies WHERE id != :targetId AND (:targetMeaning IS NULL OR vietnamese_meaning_text != :targetMeaning) AND vietnamese_meaning_text IS NOT NULL AND vietnamese_meaning_text != '' ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<String> findRandomDistractorMeanings(@Param("targetId") Long targetId, @Param("targetMeaning") String targetMeaning, @Param("limit") int limit);
}
