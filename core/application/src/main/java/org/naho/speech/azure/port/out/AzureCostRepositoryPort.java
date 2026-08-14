package org.naho.speech.azure.port.out;

import org.naho.speech.azure.model.AzureDailyCost;
import org.naho.speech.azure.result.AzureCostPointResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AzureCostRepositoryPort {

    AzureDailyCost saveOrUpdate(AzureDailyCost dailyCost);

    void saveAll(List<AzureDailyCost> dailyCosts);

    long count();

    Optional<BigDecimal> sumCostBetween(LocalDate startDate, LocalDate endDate);

    List<AzureDailyCost> findDailyCostsBetween(LocalDate fromDate, LocalDate toDate);

    List<AzureCostPointResult> findMonthlyCostsSummary(LocalDate startDate);
}
