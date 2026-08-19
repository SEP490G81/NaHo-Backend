package org.naho.cost.port.out;

import org.naho.cost.model.OpenAiDailyCost;
import org.naho.cost.result.OpenAiCostPointResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OpenAiCostRepositoryPort {
    OpenAiDailyCost saveOrUpdate(OpenAiDailyCost dailyCost);

    void saveAll(List<OpenAiDailyCost> dailyCosts);

    long count();

    Optional<BigDecimal> sumCostBetween(LocalDate startDate, LocalDate endDate);

    List<OpenAiDailyCost> findDailyCostsBetween(LocalDate fromDate, LocalDate toDate);

    List<OpenAiCostPointResult> findMonthlyCostsSummary(LocalDate startDate);
}
