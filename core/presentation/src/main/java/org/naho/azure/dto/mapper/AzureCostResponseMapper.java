package org.naho.azure.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.azure.dto.response.AzureCostChartResponse;
import org.naho.azure.dto.response.AzureCostPointResponse;
import org.naho.azure.dto.response.AzureCostSummaryResponse;
import org.naho.speech.azure.result.AzureCostChartResult;
import org.naho.speech.azure.result.AzureCostPointResult;
import org.naho.speech.azure.result.AzureCostSummaryResult;

@Mapper(componentModel = "spring")
public interface AzureCostResponseMapper {
    AzureCostSummaryResponse resultToSummaryResponse(AzureCostSummaryResult result);
    AzureCostChartResponse resultToChartResponse(AzureCostChartResult result);
    AzureCostPointResponse resultToPointResponse(AzureCostPointResult result);
}
