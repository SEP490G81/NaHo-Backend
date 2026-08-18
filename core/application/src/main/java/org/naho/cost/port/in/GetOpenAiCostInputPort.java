package org.naho.cost.port.in;

import org.naho.cost.result.OpenAiCostChartResult;
import org.naho.cost.result.OpenAiCostSummaryResult;
import org.naho.speech.llm.conversation.command.OpenAiCostQueryCommand;

public interface GetOpenAiCostInputPort {
    OpenAiCostSummaryResult getSummary();

    OpenAiCostChartResult getChartData(OpenAiCostQueryCommand command);
}
