package org.naho.point.repository;

import org.naho.point.entity.PointSummaryEntity;
import org.naho.shared.persistence.BaseJpaRepository;

import java.util.Optional;

public interface PointSummaryJpaRepository extends BaseJpaRepository<PointSummaryEntity> {
    Optional<PointSummaryEntity> findByUser_Id(Long userId);
}
