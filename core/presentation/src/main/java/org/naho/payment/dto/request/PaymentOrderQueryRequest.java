package org.naho.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.naho.payment.constant.PaymentOrderSortColumn;
import org.naho.payment.type.PaymentProvider;
import org.naho.payment.type.PaymentStatus;
import org.naho.shared.constant.SortDirection;

import java.time.Instant;

@Getter
@Setter
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrderQueryRequest {
    Integer page = 0;
    Integer size = 20;
    PaymentOrderSortColumn sortColumn = PaymentOrderSortColumn.CREATED_TIME;
    SortDirection sortDirection = SortDirection.DESC;
    String searchKeyword;
    Long userId;
    PaymentStatus status;
    PaymentProvider provider;
    Instant createdTimeFrom;
    Instant createdTimeTo;
}
