package org.naho.cost.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.cost.constant.CostProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.Dimension;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageRequest;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.Granularity;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class AwsCostExplorerClient {
    private final CostExplorerClient costExplorerClient;

    /**
     * Method lấy cost data từ [from, end]
     *
     * @param from từ ngày
     * @param to   đến ngày
     * @return GetCostAndUsageResponse
     */
    public GetCostAndUsageResponse getDailyCost(LocalDate from, LocalDate to) {
        GetCostAndUsageRequest request = GetCostAndUsageRequest.builder()
                .timePeriod(interval -> interval
                        .start(from.toString())
                        .end(to.plusDays(1).toString())
                )
                // lấy dữ liệu theo ngày
                .granularity(Granularity.DAILY)
                // chi phí AWS được tính theo đơn giá của từng dịch vụ (không trộn lại với nhau)
                .metrics(CostProperties.UNBLENDED_COST)
                // usage là tiền phát sinh do bạn thực sự dùng các dịch vụ AWS
                .filter(filter -> filter.dimensions(
                        dimensions -> dimensions
                                .key(Dimension.RECORD_TYPE)
                                .values("Usage")
                ))
                .build();

        return costExplorerClient.getCostAndUsage(request);
    }
}
