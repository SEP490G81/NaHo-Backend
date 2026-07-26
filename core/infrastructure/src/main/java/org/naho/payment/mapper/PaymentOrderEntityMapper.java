package org.naho.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.payment.entity.PaymentOrderEntity;
import org.naho.payment.model.PaymentOrder;

@Mapper(componentModel = "spring")
public interface PaymentOrderEntityMapper {

    @Mapping(target = "subscriptionPlan", ignore = true)
    @Mapping(target = "amountAmount", source = "amount.amount")
    @Mapping(target = "amountCurrency", expression = "java(domain.getAmount().currency().getCurrencyCode())")
    @Mapping(target = "createdTime", source = "createdTime")
    @Mapping(target = "modifiedTime", source = "modifiedTime")
    PaymentOrderEntity domainToEntity(PaymentOrder domain);

    default PaymentOrder entityToDomain(PaymentOrderEntity entity) {
        if (entity == null) {
            return null;
        }
        org.naho.payment.model.Money amount = new org.naho.payment.model.Money(
                entity.getAmountAmount(),
                java.util.Currency.getInstance(entity.getAmountCurrency()));
        return PaymentOrder.builder()
                .id(entity.getId())
                .orderCode(entity.getOrderCode())
                .userId(entity.getUserId())
                .subscriptionPlanId(entity.getSubscriptionPlan().getId())
                .amount(amount)
                .provider(entity.getProvider())
                .status(entity.getStatus())
                .paymentUrl(entity.getPaymentUrl())
                .providerTransactionId(entity.getProviderTransactionId())
                .createdTime(entity.getCreatedTime() != null ? entity.getCreatedTime() : java.time.Instant.now())
                .expiresTime(entity.getExpiresTime())
                .paidTime(entity.getPaidTime())
                .modifiedTime(entity.getModifiedTime())
                .build();
    }
}
