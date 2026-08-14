package org.naho.cost.usecase;

import org.naho.cost.command.AwsCostQueryCommand;
import org.naho.cost.model.AwsDailyCost;
import org.naho.cost.port.in.GetAwsCostInputPort;
import org.naho.cost.port.out.AwsCostRepositoryPort;
import org.naho.cost.result.AwsCostChartResult;
import org.naho.cost.result.AwsCostPointResult;
import org.naho.cost.result.AwsCostSummaryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class AwsCostUseCase implements GetAwsCostInputPort {

    private final AwsCostRepositoryPort awsCostRepositoryPort;

    public AwsCostUseCase(AwsCostRepositoryPort awsCostRepositoryPort) {
        this.awsCostRepositoryPort = awsCostRepositoryPort;
    }

    @Override
    public AwsCostSummaryResult getSummary() {
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = LocalDate.now();

        BigDecimal totalCost = awsCostRepositoryPort.sumCostBetween(startDate, endDate)
                .orElse(BigDecimal.ZERO);

        return new AwsCostSummaryResult(totalCost, "USD", "MonthToDate");
    }

    @Override
    public AwsCostChartResult getChartData(AwsCostQueryCommand command) {
        String timeframe = (command != null && command.getTimeframe() != null && !command.getTimeframe().isBlank())
                ? command.getTimeframe()
                : "Last6Months";

        String granularity = (command != null && command.getGranularity() != null
                && !command.getGranularity().isBlank())
                        ? command.getGranularity()
                        : "Monthly";

        if ("Daily".equalsIgnoreCase(granularity)) {
            LocalDate fromDate;
            LocalDate toDate;

            if (command != null && command.getFromDate() != null && command.getToDate() != null) {
                fromDate = command.getFromDate().toLocalDate();
                toDate = command.getToDate().toLocalDate();
            } else {
                fromDate = LocalDate.now().minusDays(30);
                toDate = LocalDate.now();
            }

            List<AwsDailyCost> dailyCosts = awsCostRepositoryPort.findDailyCostsBetween(fromDate, toDate);
            BigDecimal totalAccumulated = BigDecimal.ZERO;
            List<AwsCostPointResult> points = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            for (AwsDailyCost daily : dailyCosts) {
                BigDecimal cost = daily.getCostAmount() != null ? daily.getCostAmount() : BigDecimal.ZERO;
                totalAccumulated = totalAccumulated.add(cost);
                String dateStr = daily.getRecordDate() != null ? daily.getRecordDate().format(formatter) : "";
                String currency = daily.getCurrency() != null ? daily.getCurrency() : "USD";
                points.add(new AwsCostPointResult(dateStr, cost, currency));
            }

            return new AwsCostChartResult(totalAccumulated, "USD", "Daily", points);
        } else {
            // Default: Monthly
            LocalDate startDate;
            if ("Last3Months".equalsIgnoreCase(timeframe)) {
                startDate = LocalDate.now().minusMonths(2).with(TemporalAdjusters.firstDayOfMonth());
            } else if ("Last12Months".equalsIgnoreCase(timeframe)) {
                startDate = LocalDate.now().minusMonths(11).with(TemporalAdjusters.firstDayOfMonth());
            } else if ("Custom".equalsIgnoreCase(timeframe) && command != null && command.getFromDate() != null) {
                startDate = command.getFromDate().toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
            } else {
                // Last6Months default
                startDate = LocalDate.now().minusMonths(5).with(TemporalAdjusters.firstDayOfMonth());
            }

            List<AwsCostPointResult> points = awsCostRepositoryPort.findMonthlyCostsSummary(startDate);
            BigDecimal totalAccumulated = BigDecimal.ZERO;
            for (AwsCostPointResult p : points) {
                if (p.getCost() != null) {
                    totalAccumulated = totalAccumulated.add(p.getCost());
                }
            }
            return new AwsCostChartResult(totalAccumulated, "USD", "Monthly", points);
        }
    }
}
