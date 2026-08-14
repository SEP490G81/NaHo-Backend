package org.naho.speech.llm.usecase;

import org.naho.speech.llm.port.in.GetOpenAiCostInputPort;
import org.naho.speech.llm.port.out.OpenAiCostPort;
import org.naho.speech.llm.result.OpenAiCostStatResult;

import java.time.YearMonth;

public class GetOpenAiCostUseCase implements GetOpenAiCostInputPort {

    private final OpenAiCostPort openAiCostPort;

    public GetOpenAiCostUseCase(OpenAiCostPort openAiCostPort) {
        this.openAiCostPort = openAiCostPort;
    }

    @Override
    public OpenAiCostStatResult getMonthlyCostStat(YearMonth yearMonth) {
        return openAiCostPort.getMonthlyCostStat(yearMonth);
    }

    @Override
    public OpenAiCostStatResult getAllDailyCosts() {
        return openAiCostPort.getAllDailyCosts();
    }
}
