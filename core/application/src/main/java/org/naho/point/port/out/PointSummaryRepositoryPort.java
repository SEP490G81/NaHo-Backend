package org.naho.point.port.out;

import org.naho.point.model.PointSummary;

import java.util.Optional;

public interface PointSummaryRepositoryPort {
    Optional<PointSummary> findById(Long id);

    PointSummary save(PointSummary pointSummary);
}
