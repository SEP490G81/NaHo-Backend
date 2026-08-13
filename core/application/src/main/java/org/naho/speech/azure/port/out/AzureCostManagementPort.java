package org.naho.speech.azure.port.out;

import org.naho.speech.azure.command.AzureCostQueryCommand;
import org.naho.speech.azure.result.AzureCostChartResult;
import org.naho.speech.azure.result.AzureCostSummaryResult;

public interface AzureCostManagementPort {
    AzureCostSummaryResult fetchCostSummary(String timeframe);
    AzureCostChartResult fetchCostChart(AzureCostQueryCommand command);
}
