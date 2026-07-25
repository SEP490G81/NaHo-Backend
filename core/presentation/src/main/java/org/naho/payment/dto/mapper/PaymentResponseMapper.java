package org.naho.payment.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.payment.dto.response.*;
import org.naho.payment.result.CancelPaymentResult;
import org.naho.payment.result.CreatePaymentResult;
import org.naho.payment.result.PaymentOrderResult;

@Mapper(componentModel = "spring")
public interface PaymentResponseMapper {

    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", expression = "java(result.amount().currency().getCurrencyCode())")
    @Mapping(target = "paymentUrl", expression = "java(result.paymentUrl().toString())")
    CreatePaymentResponse resultToCreateResponse(CreatePaymentResult result);

    @Mapping(target = "amount", source = "amount.amount")
    @Mapping(target = "currency", expression = "java(result.amount().currency().getCurrencyCode())")
    PaymentOrderResponse resultToOrderResponse(PaymentOrderResult result);

    @Mapping(target = "status", expression = "java(result.status().name())")
    CancelPaymentResponse resultToCancelResponse(CancelPaymentResult result);

    VnPayIpnResponse toVnPayIpnResponse(String rspCode, String message);

    VnPayReturnResponse toVnPayReturnResponse(String orderCode, String status, String message);
}
