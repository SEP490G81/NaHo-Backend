package org.naho.cost.llm.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.cost.llm.dto.response.OpenAiCostChartResponse;
import org.naho.cost.llm.dto.response.OpenAiCostPointResponse;
import org.naho.cost.llm.dto.response.OpenAiCostSummaryResponse;
import org.naho.cost.result.OpenAiCostChartResult;
import org.naho.cost.result.OpenAiCostPointResult;
import org.naho.cost.result.OpenAiCostSummaryResult;

@Mapper(componentModel = "spring")
public interface OpenAiCostResponseMapper {
    OpenAiCostSummaryResponse resultToSummaryResponse(OpenAiCostSummaryResult result);

    OpenAiCostChartResponse resultToChartResponse(OpenAiCostChartResult result);

    OpenAiCostPointResponse resultToPointResponse(OpenAiCostPointResult result);
}
