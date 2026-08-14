package org.naho.speech.llm.port.in;

import org.naho.speech.llm.result.OpenAiCostStatResult;

import java.time.YearMonth;

public interface GetOpenAiCostInputPort {
    OpenAiCostStatResult getMonthlyCostStat(YearMonth yearMonth);
    OpenAiCostStatResult getAllDailyCosts();
}
