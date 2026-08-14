package org.naho.cost.port.in;

import org.naho.cost.command.AwsCostQueryCommand;
import org.naho.cost.result.AwsCostChartResult;
import org.naho.cost.result.AwsCostSummaryResult;

public interface GetAwsCostInputPort {
    AwsCostSummaryResult getSummary();

    AwsCostChartResult getChartData(AwsCostQueryCommand command);
}
