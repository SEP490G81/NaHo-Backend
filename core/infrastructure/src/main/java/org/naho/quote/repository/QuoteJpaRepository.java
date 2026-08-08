package org.naho.quote.repository;

import org.naho.quote.entity.QuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuoteJpaRepository extends JpaRepository<QuoteEntity, Long> {

    @Query(value = "SELECT * FROM quote ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<QuoteEntity> findRandomQuote();

    boolean existsBy();
}
