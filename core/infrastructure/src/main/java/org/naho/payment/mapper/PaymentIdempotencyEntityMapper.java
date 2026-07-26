package org.naho.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.payment.entity.PaymentIdempotencyEntity;
import org.naho.payment.model.PaymentIdempotency;

@Mapper(componentModel = "spring")
public interface PaymentIdempotencyEntityMapper {

    @Mapping(target = "createdTime", source = "createdTime")
    @Mapping(target = "modifiedTime", ignore = true)
    PaymentIdempotencyEntity domainToEntity(PaymentIdempotency domain);

    PaymentIdempotency entityToDomain(PaymentIdempotencyEntity entity);
}
