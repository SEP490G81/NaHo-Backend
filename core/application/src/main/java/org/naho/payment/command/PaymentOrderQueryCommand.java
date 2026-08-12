package org.naho.payment.command;

import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.payment.constant.PaymentOrderSortColumn;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.type.PaymentProvider;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.constant.SortDirection;
import org.naho.shared.exception.ApplicationException;

import java.time.Instant;

public record PaymentOrderQueryCommand(
        Integer page,
        Integer size,
        PaymentOrderSortColumn sortColumn,
        SortDirection sortDirection,
        String searchKeyword,
        Long userId,
        PaymentStatus status,
        PaymentProvider provider,
        Instant createdTimeFrom,
        Instant createdTimeTo
) {
    public PaymentOrderQueryCommand {
        if (page != null && page < 0) {
            throw new ApplicationException(
                    PaymentErrorCode.PAYMENT_PAGE_INVALID,
                    PaymentDetailMessageKey.PAYMENT_PAGE_INVALID
            );
        }

        if (size != null && (size < 20 || size > 100)) {
            throw new ApplicationException(
                    PaymentErrorCode.PAYMENT_SIZE_INVALID,
                    PaymentDetailMessageKey.PAYMENT_SIZE_INVALID
            );
        }

        if (createdTimeFrom != null && createdTimeTo != null && createdTimeFrom.isAfter(createdTimeTo)) {
            throw new ApplicationException(
                    PaymentErrorCode.PAYMENT_CREATED_TIME_RANGE_INVALID,
                    PaymentDetailMessageKey.PAYMENT_CREATED_TIME_RANGE_INVALID
            );
        }
    }
}
