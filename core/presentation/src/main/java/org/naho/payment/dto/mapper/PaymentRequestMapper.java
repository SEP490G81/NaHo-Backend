package org.naho.payment.dto.mapper;

import org.mapstruct.Mapper;
import org.naho.payment.command.PaymentOrderQueryCommand;
import org.naho.payment.dto.request.PaymentOrderQueryRequest;

@Mapper(componentModel = "spring")
public interface PaymentRequestMapper {
    PaymentOrderQueryCommand requestToCommand(PaymentOrderQueryRequest request);
}
