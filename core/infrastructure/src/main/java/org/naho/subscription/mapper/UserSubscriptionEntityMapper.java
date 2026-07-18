package org.naho.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.subscription.entity.UserSubscriptionEntity;
import org.naho.subscription.model.UserSubscription;

@Mapper(componentModel = "spring")
public interface UserSubscriptionEntityMapper {

    @Mapping(target = "subscriptionPlan", ignore = true)
    @Mapping(target = "paymentOrder", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    UserSubscriptionEntity domainToEntity(UserSubscription domain);

    @Mapping(target = "subscriptionPlanId", source = "subscriptionPlan.id")
    @Mapping(target = "paymentOrderId", source = "paymentOrder.id")
    UserSubscription entityToDomain(UserSubscriptionEntity entity);
}
