package org.naho.cost.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.cost.llm.dto.response.OpenAiCostChartResponse;
import org.naho.cost.llm.dto.response.OpenAiCostPointResponse;
import org.naho.cost.llm.dto.response.OpenAiCostSummaryResponse;
import org.naho.speech.llm.result.OpenAiCostChartResult;
import org.naho.speech.llm.result.OpenAiCostPointResult;
import org.naho.speech.llm.result.OpenAiCostSummaryResult;

@Mapper(componentModel = "spring")
public interface OpenAiCostResponseMapper {
    OpenAiCostSummaryResponse resultToSummaryResponse(OpenAiCostSummaryResult result);

    OpenAiCostChartResponse resultToChartResponse(OpenAiCostChartResult result);

    OpenAiCostPointResponse resultToPointResponse(OpenAiCostPointResult result);
}
