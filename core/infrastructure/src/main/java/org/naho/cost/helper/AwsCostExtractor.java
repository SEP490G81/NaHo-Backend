package org.naho.cost.helper;

import lombok.RequiredArgsConstructor;
import org.naho.cost.constant.CostProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.costexplorer.model.MetricValue;
import software.amazon.awssdk.services.costexplorer.model.ResultByTime;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class AwsCostExtractor {
    /**
     * Lấy chi phí của 1 ResultByTime
     *
     * @param resultByTime dữ liệu cost của 1 khoảng thời gian
     * @return total cost (BigDecimal)
     */
    public BigDecimal extractCost(ResultByTime resultByTime) {
        MetricValue metricValue = resultByTime.total().get(CostProperties.UNBLENDED_COST);
        if (metricValue == null || metricValue.amount() == null) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(metricValue.amount());
    }

    /**
     * Lấy currency của một ResultByTime.
     *
     * @param resultByTime dữ liệu cost của một khoảng thời gian
     * @return currency code
     */
    public String extractCurrency(ResultByTime resultByTime) {
        MetricValue metricValue = resultByTime.total().get(CostProperties.UNBLENDED_COST);
        if (metricValue == null || metricValue.unit() == null) {
            return CostProperties.USD_CURRENCY;
        }
        return metricValue.unit();
    }
}
