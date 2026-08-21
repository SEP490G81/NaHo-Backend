package org.naho.cost.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.cost.constant.CostProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.costexplorer.CostExplorerClient;
import software.amazon.awssdk.services.costexplorer.model.CostExplorerException;
import software.amazon.awssdk.services.costexplorer.model.Dimension;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageRequest;
import software.amazon.awssdk.services.costexplorer.model.GetCostAndUsageResponse;
import software.amazon.awssdk.services.costexplorer.model.Granularity;

import java.time.LocalDate;
import java.time.ZoneOffset;

@Slf4j
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
        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);

        LocalDate startDate = from;
        LocalDate endDate = to.plusDays(1);

        if (endDate.isAfter(todayUtc)) {
            endDate = todayUtc;
        }

        if (!startDate.isBefore(endDate)) {
            startDate = endDate.minusDays(1);
        }

        String startStr = startDate.toString();
        String endStr = endDate.toString();

        GetCostAndUsageRequest request = GetCostAndUsageRequest.builder()
                .timePeriod(interval -> interval
                        .start(startStr)
                        .end(endStr)
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

        try {
            return costExplorerClient.getCostAndUsage(request);
        } catch (CostExplorerException e) {
            log.error("AWS CostExplorer API Error [Code={}]: {}",
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorCode() : "UNKNOWN",
                    e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage());
            throw e;
        }
    }
}
