package org.naho.point.command;

import org.naho.i18n.message.point.PointHistoryDetailMessageKey;
import org.naho.point.constant.PointAmountType;
import org.naho.point.constant.PointHistorySortColumn;
import org.naho.point.exception.PointHistoryErrorCode;
import org.naho.point.type.PointTransactionType;
import org.naho.shared.constant.SortDirection;
import org.naho.shared.exception.ApplicationException;

import java.time.Instant;

public record PointHistoryQueryCommand(
        Integer page,
        Integer size,
        PointHistorySortColumn sortColumn,
        SortDirection sortDirection,
        PointTransactionType transactionType,
        PointAmountType amountType,
        Instant transactionTimeFrom,
        Instant transactionTimeTo
) {
    public PointHistoryQueryCommand {
        if (page < 0) {
            throw new ApplicationException(
                    PointHistoryErrorCode.POINT_HISTORY_PAGE_INVALID,
                    PointHistoryDetailMessageKey.POINT_HISTORY_PAGE_INVALID
            );
        }

        if (size < 0 || size > 100) {
            throw new ApplicationException(
                    PointHistoryErrorCode.POINT_HISTORY_SIZE_INVALID,
                    PointHistoryDetailMessageKey.POINT_HISTORY_SIZE_INVALID
            );
        }

        if (transactionTimeFrom != null &&
                transactionTimeTo != null &&
                // strictly greater than
                transactionTimeFrom.isAfter(transactionTimeTo)) {
            throw new ApplicationException(
                    PointHistoryErrorCode.POINT_HISTORY_TRANSACTION_TIME_RANGE_INVALID,
                    PointHistoryDetailMessageKey.POINT_HISTORY_TRANSACTION_TIME_RANGE_INVALID
            );
        }
    }
}
