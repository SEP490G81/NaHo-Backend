package org.naho.point.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.point.constant.PointAmountType;
import org.naho.point.constant.PointHistorySortColumn;
import org.naho.point.type.PointTransactionType;
import org.naho.shared.constant.SortDirection;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryQueryRequest {
    Integer page = 0;
    Integer size = 20;
    PointHistorySortColumn sortColumn = PointHistorySortColumn.TRANSACTION_TIME;
    SortDirection sortDirection = SortDirection.DESC;
    PointTransactionType transactionType;
    PointAmountType amountType;
    Instant transactionTimeFrom;
    Instant transactionTimeTo;
}
