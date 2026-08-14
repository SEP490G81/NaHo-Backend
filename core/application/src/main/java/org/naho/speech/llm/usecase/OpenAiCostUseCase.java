package org.naho.speech.llm.usecase;

import org.naho.cost.model.OpenAiDailyCost;
import org.naho.speech.llm.command.OpenAiCostQueryCommand;
import org.naho.speech.llm.port.in.GetOpenAiCostInputPort;
import org.naho.speech.llm.port.out.OpenAiCostRepositoryPort;
import org.naho.speech.llm.result.OpenAiCostChartResult;
import org.naho.speech.llm.result.OpenAiCostPointResult;
import org.naho.speech.llm.result.OpenAiCostSummaryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class OpenAiCostUseCase implements GetOpenAiCostInputPort {

    private final OpenAiCostRepositoryPort openAiCostRepositoryPort;

    public OpenAiCostUseCase(OpenAiCostRepositoryPort openAiCostRepositoryPort) {
        this.openAiCostRepositoryPort = openAiCostRepositoryPort;
    }

    @Override
    public OpenAiCostSummaryResult getSummary() {
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = LocalDate.now();

        BigDecimal totalCost = openAiCostRepositoryPort.sumCostBetween(startDate, endDate)
                .orElse(BigDecimal.ZERO);

        return new OpenAiCostSummaryResult(totalCost, "USD", "MonthToDate");
    }

    @Override
    public OpenAiCostChartResult getChartData(OpenAiCostQueryCommand command) {
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

            List<OpenAiDailyCost> dailyCosts = openAiCostRepositoryPort.findDailyCostsBetween(fromDate, toDate);
            BigDecimal totalAccumulated = BigDecimal.ZERO;
            List<OpenAiCostPointResult> points = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            for (OpenAiDailyCost daily : dailyCosts) {
                BigDecimal cost = daily.getCostAmount() != null ? daily.getCostAmount() : BigDecimal.ZERO;
                totalAccumulated = totalAccumulated.add(cost);
                String dateStr = daily.getRecordDate() != null ? daily.getRecordDate().format(formatter) : "";
                String currency = daily.getCurrency() != null ? daily.getCurrency() : "USD";
                points.add(new OpenAiCostPointResult(dateStr, cost, currency));
            }

            return new OpenAiCostChartResult(totalAccumulated, "USD", "Daily", points);
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

            List<OpenAiCostPointResult> points = openAiCostRepositoryPort.findMonthlyCostsSummary(startDate);
            BigDecimal totalAccumulated = BigDecimal.ZERO;
            for (OpenAiCostPointResult p : points) {
                if (p.getCost() != null) {
                    totalAccumulated = totalAccumulated.add(p.getCost());
                }
            }

            return new OpenAiCostChartResult(totalAccumulated, "USD", "Monthly", points);
        }
    }
}
