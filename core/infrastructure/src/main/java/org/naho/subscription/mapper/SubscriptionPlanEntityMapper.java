package org.naho.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.naho.payment.model.Money;
import org.naho.subscription.entity.SubscriptionPlanEntity;
import org.naho.subscription.model.SubscriptionPlan;

import java.util.Currency;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface SubscriptionPlanEntityMapper {

    @Mapping(target = "priceAmount", source = "price.amount")
    @Mapping(target = "priceCurrency", source = "price", qualifiedByName = "currencyCode")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SubscriptionPlanEntity domainToEntity(SubscriptionPlan domain);

    @Mapping(target = "price", source = ".", qualifiedByName = "toMoney")
    SubscriptionPlan entityToDomain(SubscriptionPlanEntity entity);

    @Named("currencyCode")
    default String currencyCode(Money money) {
        return money.currency().getCurrencyCode();
    }

    @Named("toMoney")
    default Money toMoney(SubscriptionPlanEntity entity) {
        if (entity.getPriceAmount() == null || entity.getPriceCurrency() == null) {
            return null;
        }

        return new Money(
                entity.getPriceAmount(),
                Currency.getInstance(entity.getPriceCurrency())
        );
    }
}