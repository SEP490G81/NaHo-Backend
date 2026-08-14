package org.naho.speech.llm.repository;

import org.naho.speech.llm.entity.OpenAiDailyCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OpenAiDailyCostJpaRepository extends JpaRepository<OpenAiDailyCostEntity, Long> {

    Optional<OpenAiDailyCostEntity> findByRecordDate(LocalDate recordDate);

    List<OpenAiDailyCostEntity> findByRecordDateBetweenOrderByRecordDateAsc(LocalDate startDate, LocalDate endDate);

    List<OpenAiDailyCostEntity> findAllByOrderByRecordDateAsc();

    @Query("SELECT MAX(c.recordDate) FROM OpenAiDailyCostEntity c")
    Optional<LocalDate> findMaxRecordDate();
}
