package org.naho.question.repository;

import org.naho.grammar.entity.GrammarEntity;
import org.naho.shared.persistence.BaseJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface GrammarJpaRepository extends BaseJpaRepository<GrammarEntity> {
    List<GrammarEntity> findAllByIdIn(Collection<Long> ids);


    @Query("SELECT g FROM GrammarEntity g WHERE " +
            "LOWER(g.japanese) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.reading) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(g.vietnameseMeaningText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<GrammarEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
