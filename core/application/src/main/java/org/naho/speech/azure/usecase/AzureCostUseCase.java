package org.naho.speech.azure.usecase;

import org.naho.speech.azure.command.AzureCostQueryCommand;
import org.naho.speech.azure.model.AzureDailyCost;
import org.naho.speech.azure.port.in.GetAzureCostInputPort;
import org.naho.speech.azure.port.out.AzureCostRepositoryPort;
import org.naho.speech.azure.result.AzureCostChartResult;
import org.naho.speech.azure.result.AzureCostPointResult;
import org.naho.speech.azure.result.AzureCostSummaryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class AzureCostUseCase implements GetAzureCostInputPort {

    private final AzureCostRepositoryPort azureCostRepositoryPort;

    public AzureCostUseCase(AzureCostRepositoryPort azureCostRepositoryPort) {
        this.azureCostRepositoryPort = azureCostRepositoryPort;
    }

    @Override
    public AzureCostSummaryResult getSummary() {
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = LocalDate.now();

        BigDecimal totalCost = azureCostRepositoryPort.sumCostBetween(startDate, endDate)
                .orElse(BigDecimal.ZERO);

        return new AzureCostSummaryResult(totalCost, "USD", "MonthToDate");
    }

    @Override
    public AzureCostChartResult getChartData(AzureCostQueryCommand command) {
        String timeframe = (command != null && command.getTimeframe() != null && !command.getTimeframe().isBlank())
                ? command.getTimeframe()
                : "Last6Months";

        String granularity = (command != null && command.getGranularity() != null && !command.getGranularity().isBlank())
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

            List<AzureDailyCost> dailyCosts = azureCostRepositoryPort.findDailyCostsBetween(fromDate, toDate);
            BigDecimal totalAccumulated = BigDecimal.ZERO;
            List<AzureCostPointResult> points = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            for (AzureDailyCost daily : dailyCosts) {
                BigDecimal cost = daily.getCostAmount() != null ? daily.getCostAmount() : BigDecimal.ZERO;
                totalAccumulated = totalAccumulated.add(cost);
                String dateStr = daily.getRecordDate() != null ? daily.getRecordDate().format(formatter) : "";
                String currency = daily.getCurrency() != null ? daily.getCurrency() : "USD";
                points.add(new AzureCostPointResult(dateStr, cost, currency));
            }

            return new AzureCostChartResult(totalAccumulated, "USD", "Daily", points);
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

            List<AzureCostPointResult> points = azureCostRepositoryPort.findMonthlyCostsSummary(startDate);
            BigDecimal totalAccumulated = BigDecimal.ZERO;
            for (AzureCostPointResult p : points) {
                if (p.getCost() != null) {
                    totalAccumulated = totalAccumulated.add(p.getCost());
                }
            }

            return new AzureCostChartResult(totalAccumulated, "USD", "Monthly", points);
        }
    }
}
