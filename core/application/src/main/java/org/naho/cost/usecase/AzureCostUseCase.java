package org.naho.cost.usecase;

import org.naho.cost.command.AzureCostQueryCommand;
import org.naho.cost.constant.CostProperties;
import org.naho.cost.model.AzureDailyCost;
import org.naho.cost.port.in.GetAzureCostInputPort;
import org.naho.cost.port.out.AzureCostRepositoryPort;
import org.naho.cost.result.AzureCostChartResult;
import org.naho.cost.result.AzureCostPointResult;
import org.naho.cost.result.AzureCostSummaryResult;
import org.naho.shared.constant.SystemZoneId;

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
        LocalDate startDate = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID).with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);

        BigDecimal totalCost = azureCostRepositoryPort.sumCostBetween(startDate, endDate)
                .orElse(BigDecimal.ZERO);

        return new AzureCostSummaryResult(totalCost, CostProperties.USD_CURRENCY, "MonthToDate");
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
                fromDate = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID).minusDays(30);
                toDate = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID);
            }

            List<AzureDailyCost> dailyCosts = azureCostRepositoryPort.findDailyCostsBetween(fromDate, toDate);
            BigDecimal totalAccumulated = BigDecimal.ZERO;
            List<AzureCostPointResult> points = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            for (AzureDailyCost daily : dailyCosts) {
                BigDecimal cost = daily.getCostAmount() != null ? daily.getCostAmount() : BigDecimal.ZERO;
                totalAccumulated = totalAccumulated.add(cost);
                String dateStr = daily.getRecordDate() != null ? daily.getRecordDate().format(formatter) : "";
                String currency = daily.getCurrency() != null ? daily.getCurrency() : CostProperties.USD_CURRENCY;
                points.add(new AzureCostPointResult(dateStr, cost, currency));
            }

            return new AzureCostChartResult(totalAccumulated, CostProperties.USD_CURRENCY, "Daily", points);
        } else {
            // Default: Monthly
            LocalDate startDate;
            if ("Last3Months".equalsIgnoreCase(timeframe)) {
                startDate = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID).minusMonths(2).with(TemporalAdjusters.firstDayOfMonth());
            } else if ("Last12Months".equalsIgnoreCase(timeframe)) {
                startDate = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID).minusMonths(11).with(TemporalAdjusters.firstDayOfMonth());
            } else if ("Custom".equalsIgnoreCase(timeframe) && command.getFromDate() != null) {
                startDate = command.getFromDate().toLocalDate().with(TemporalAdjusters.firstDayOfMonth());
            } else {
                // Last6Months default
                startDate = LocalDate.now(SystemZoneId.HO_CHI_MINH_ZONE_ID).minusMonths(5).with(TemporalAdjusters.firstDayOfMonth());
            }

            List<AzureCostPointResult> points = azureCostRepositoryPort.findMonthlyCostsSummary(startDate);
            BigDecimal totalAccumulated = BigDecimal.ZERO;
            for (AzureCostPointResult p : points) {
                if (p.getCost() != null) {
                    totalAccumulated = totalAccumulated.add(p.getCost());
                }
            }
            return new AzureCostChartResult(totalAccumulated, CostProperties.USD_CURRENCY, "Monthly", points);
        }
    }
}
