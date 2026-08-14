package org.naho.speech.azure.port.in;

import org.naho.speech.azure.command.AzureCostQueryCommand;
import org.naho.speech.azure.result.AzureCostChartResult;
import org.naho.speech.azure.result.AzureCostSummaryResult;

public interface GetAzureCostInputPort {
    AzureCostSummaryResult getSummary();
    AzureCostChartResult getChartData(AzureCostQueryCommand command);
}
