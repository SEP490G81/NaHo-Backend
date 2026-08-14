package org.naho.speech.llm.port.out;

import org.naho.speech.llm.result.OpenAiCostStatResult;

import java.time.YearMonth;

public interface OpenAiCostPort {
    OpenAiCostStatResult getMonthlyCostStat(YearMonth yearMonth);
    OpenAiCostStatResult getAllDailyCosts();
}
