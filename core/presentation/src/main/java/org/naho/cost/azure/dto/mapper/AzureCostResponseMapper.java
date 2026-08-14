package org.naho.cost.azure.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.cost.azure.dto.response.AzureCostChartResponse;
import org.naho.cost.azure.dto.response.AzureCostPointResponse;
import org.naho.cost.azure.dto.response.AzureCostSummaryResponse;
import org.naho.cost.result.AzureCostChartResult;
import org.naho.cost.result.AzureCostPointResult;
import org.naho.cost.result.AzureCostSummaryResult;

@Mapper(componentModel = "spring")
public interface AzureCostResponseMapper {
    AzureCostSummaryResponse resultToSummaryResponse(AzureCostSummaryResult result);

    AzureCostChartResponse resultToChartResponse(AzureCostChartResult result);

    AzureCostPointResponse resultToPointResponse(AzureCostPointResult result);
}
