package org.naho.speech.llm.port.in;

import org.naho.speech.llm.command.OpenAiCostQueryCommand;
import org.naho.speech.llm.result.OpenAiCostChartResult;
import org.naho.speech.llm.result.OpenAiCostSummaryResult;

public interface GetOpenAiCostInputPort {
    OpenAiCostSummaryResult getSummary();
    OpenAiCostChartResult getChartData(OpenAiCostQueryCommand command);
}
