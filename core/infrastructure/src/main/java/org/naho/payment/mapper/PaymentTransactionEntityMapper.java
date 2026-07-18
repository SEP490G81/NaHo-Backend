package org.naho.payment.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.payment.entity.PaymentTransactionEntity;
import org.naho.payment.model.PaymentTransaction;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface PaymentTransactionEntityMapper {

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Mapping(target = "paymentOrder", ignore = true)
    @Mapping(target = "amountAmount", source = "amount.amount")
    @Mapping(target = "amountCurrency", expression = "java(domain.getAmount().currency().getCurrencyCode())")
    @Mapping(target = "metadata", expression = "java(mapToJson(domain.getMetadata()))")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    PaymentTransactionEntity domainToEntity(PaymentTransaction domain);

    default PaymentTransaction entityToDomain(PaymentTransactionEntity entity) {
        if (entity == null) {
            return null;
        }
        org.naho.payment.model.Money amount = new org.naho.payment.model.Money(
                entity.getAmountAmount(),
                java.util.Currency.getInstance(entity.getAmountCurrency())
        );
        return PaymentTransaction.builder()
                .id(entity.getId())
                .paymentOrderId(entity.getPaymentOrder().getId())
                .provider(entity.getProvider())
                .providerTransactionId(entity.getProviderTransactionId())
                .amount(amount)
                .successful(entity.getSuccessful())
                .providerTransactionTime(entity.getProviderTransactionTime())
                .metadata(jsonToMap(entity.getMetadata()))
                .createdTime(entity.getCreatedTime())
                .build();
    }

    default String mapToJson(Map<String, String> map) {
        if (map == null) return null;
        try {
            return OBJECT_MAPPER.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    default Map<String, String> jsonToMap(String json) {
        if (json == null || json.isBlank()) return Map.of();
        try {
            return OBJECT_MAPPER.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (JsonProcessingException e) {
            return Map.of();
        }
    }
}
