package org.naho.cost.aws.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.cost.aws.dto.response.AwsCostChartResponse;
import org.naho.cost.aws.dto.response.AwsCostPointResponse;
import org.naho.cost.aws.dto.response.AwsCostSummaryResponse;
import org.naho.cost.result.AwsCostChartResult;
import org.naho.cost.result.AwsCostPointResult;
import org.naho.cost.result.AwsCostSummaryResult;

@Mapper(componentModel = "spring")
public interface AwsCostResponseMapper {
    AwsCostSummaryResponse resultToSummaryResponse(AwsCostSummaryResult result);

    AwsCostChartResponse resultToChartResponse(AwsCostChartResult result);

    AwsCostPointResponse resultToPointResponse(AwsCostPointResult result);
}
