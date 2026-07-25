package org.naho.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.naho.subscription.entity.SubscriptionPlanEntity;
import org.naho.subscription.model.SubscriptionPlan;

@Mapper(componentModel = "spring")
public interface SubscriptionPlanEntityMapper {

    @Mapping(target = "priceAmount", source = "price.amount")
    @Mapping(target = "priceCurrency", expression = "java(domain.getPrice().currency().getCurrencyCode())")
    @Mapping(target = "monthlyAssessmentLimit", source = "quota.monthlyAssessmentLimit")
    @Mapping(target = "monthlyAssessmentAudioSeconds", source = "quota.monthlyAssessmentAudioSeconds")
    @Mapping(target = "maxAssessmentAudioSeconds", source = "quota.maxAssessmentAudioSeconds")
    @Mapping(target = "monthlyConversationSeconds", source = "quota.monthlyConversationSeconds")
    @Mapping(target = "maxConversationSessionSeconds", source = "quota.maxConversationSessionSeconds")
    @Mapping(target = "maxConversationTurnsPerSession", source = "quota.maxConversationTurnsPerSession")
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    SubscriptionPlanEntity domainToEntity(SubscriptionPlan domain);

    default SubscriptionPlan entityToDomain(SubscriptionPlanEntity entity) {
        if (entity == null) {
            return null;
        }
        org.naho.payment.model.Money price = new org.naho.payment.model.Money(
                entity.getPriceAmount(),
                java.util.Currency.getInstance(entity.getPriceCurrency())
        );
        org.naho.subscription.model.UsageQuota quota = new org.naho.subscription.model.UsageQuota(
                entity.getMonthlyAssessmentLimit(),
                entity.getMonthlyAssessmentAudioSeconds(),
                entity.getMaxAssessmentAudioSeconds(),
                entity.getMonthlyConversationSeconds(),
                entity.getMaxConversationSessionSeconds(),
                entity.getMaxConversationTurnsPerSession()
        );
        return SubscriptionPlan.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .description(entity.getDescription())
                .tier(entity.getTier())
                .price(price)
                .durationDays(entity.getDurationDays())
                .quota(quota)
                .fullCurriculumAccess(entity.getFullCurriculumAccess())
                .progressAnalyticsEnabled(entity.getProgressAnalyticsEnabled())
                .sampleAnswerEnabled(entity.getSampleAnswerEnabled())
                .status(entity.getStatus())
                .createdTime(entity.getCreatedTime())
                .modifiedTime(entity.getModifiedTime())
                .build();
    }
}
