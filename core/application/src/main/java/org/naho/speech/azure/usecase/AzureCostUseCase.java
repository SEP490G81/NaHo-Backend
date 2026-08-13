package org.naho.speech.azure.usecase;

import org.naho.speech.azure.command.AzureCostQueryCommand;
import org.naho.speech.azure.port.in.GetAzureCostInputPort;
import org.naho.speech.azure.port.out.AzureCostManagementPort;
import org.naho.speech.azure.result.AzureCostChartResult;
import org.naho.speech.azure.result.AzureCostSummaryResult;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;

public class AzureCostUseCase implements GetAzureCostInputPort {

    private final AzureCostManagementPort azureCostManagementPort;

    public AzureCostUseCase(AzureCostManagementPort azureCostManagementPort) {
        this.azureCostManagementPort = azureCostManagementPort;
    }

    @Override
    public AzureCostSummaryResult getSummary() {
        return azureCostManagementPort.fetchCostSummary("MonthToDate");
    }

    @Override
    public AzureCostChartResult getChartData(AzureCostQueryCommand command) {
        AzureCostQueryCommand effectiveCommand = processCommand(command);
        return azureCostManagementPort.fetchCostChart(effectiveCommand);
    }

    private AzureCostQueryCommand processCommand(AzureCostQueryCommand command) {
        String timeframe = (command != null && command.getTimeframe() != null && !command.getTimeframe().isBlank())
                ? command.getTimeframe()
                : "Last6Months";

        String granularity = (command != null && command.getGranularity() != null && !command.getGranularity().isBlank())
                ? command.getGranularity()
                : "Monthly";

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);

        if ("Last3Months".equalsIgnoreCase(timeframe)) {
            ZonedDateTime fromDate = now.minusMonths(2).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime toDate = now;
            return new AzureCostQueryCommand("Custom", granularity, fromDate, toDate);
        } else if ("Last6Months".equalsIgnoreCase(timeframe)) {
            ZonedDateTime fromDate = now.minusMonths(5).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime toDate = now;
            return new AzureCostQueryCommand("Custom", granularity, fromDate, toDate);
        } else if ("Last12Months".equalsIgnoreCase(timeframe)) {
            ZonedDateTime fromDate = now.minusMonths(11).with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay(ZoneOffset.UTC);
            ZonedDateTime toDate = now;
            return new AzureCostQueryCommand("Custom", granularity, fromDate, toDate);
        }

        return new AzureCostQueryCommand(
                timeframe,
                granularity,
                command != null ? command.getFromDate() : null,
                command != null ? command.getToDate() : null
        );
    }
}
