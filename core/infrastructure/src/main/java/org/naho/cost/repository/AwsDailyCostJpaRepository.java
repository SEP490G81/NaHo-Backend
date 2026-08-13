package org.naho.cost.repository;

import org.naho.cost.entity.AwsDailyCostEntity;
import org.naho.shared.persistence.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface AwsDailyCostJpaRepository extends BaseJpaRepository<AwsDailyCostEntity> {
    Optional<AwsDailyCostEntity> findByRecordDate(LocalDate recordDate);

    @Query("SELECT MAX(c.recordDate) FROM AwsDailyCostEntity c")
    LocalDate findMaxRecordDate();
}
