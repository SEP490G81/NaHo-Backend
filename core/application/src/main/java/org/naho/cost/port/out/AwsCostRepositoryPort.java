package org.naho.cost.port.out;

import org.naho.cost.model.AwsDailyCost;
import org.naho.cost.result.AwsCostPointResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AwsCostRepositoryPort {

    Optional<BigDecimal> sumCostBetween(LocalDate startDate, LocalDate endDate);

    List<AwsDailyCost> findDailyCostsBetween(LocalDate fromDate, LocalDate toDate);

    List<AwsCostPointResult> findMonthlyCostsSummary(LocalDate startDate);
}
