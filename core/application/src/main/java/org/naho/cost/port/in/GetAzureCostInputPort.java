package org.naho.cost.port.in;

import org.naho.cost.command.AzureCostQueryCommand;
import org.naho.cost.result.AzureCostChartResult;
import org.naho.cost.result.AzureCostSummaryResult;

public interface GetAzureCostInputPort {
    AzureCostSummaryResult getSummary();

    AzureCostChartResult getChartData(AzureCostQueryCommand command);
}
