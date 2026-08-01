package org.naho.subscription.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.payment.entity.PaymentOrderEntity;
import org.naho.payment.repository.PaymentOrderJpaRepository;
import org.naho.subscription.entity.SubscriptionPlanEntity;
import org.naho.subscription.entity.UserSubscriptionEntity;
import org.naho.subscription.mapper.UserSubscriptionEntityMapper;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.repository.SubscriptionPlanJpaRepository;
import org.naho.subscription.repository.UserSubscriptionJpaRepository;
import org.naho.subscription.type.SubscriptionStatus;
import org.naho.user.entity.UserEntity;
import org.naho.user.repository.UserJpaRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserSubscriptionRepositoryAdapter implements UserSubscriptionRepositoryPort {

    private final UserSubscriptionJpaRepository subscriptionJpaRepository;
    private final SubscriptionPlanJpaRepository planJpaRepository;
    private final PaymentOrderJpaRepository orderJpaRepository;
    private final UserJpaRepository userJpaRepository;
    private final UserSubscriptionEntityMapper subscriptionEntityMapper;

    @Override
    public UserSubscription save(UserSubscription subscription) {
        UserSubscriptionEntity entity = subscriptionEntityMapper.domainToEntity(subscription);

        UserEntity userEntity = userJpaRepository.getReferenceById(subscription.getUserId());
        entity.setUser(userEntity);

        SubscriptionPlanEntity planEntity = planJpaRepository.getReferenceById(subscription.getSubscriptionPlanId());
        entity.setSubscriptionPlan(planEntity);

        if (subscription.getPaymentOrderId() != null) {
            PaymentOrderEntity orderEntity = orderJpaRepository.getReferenceById(subscription.getPaymentOrderId());
            entity.setPaymentOrder(orderEntity);
        }

        UserSubscriptionEntity savedEntity = subscriptionJpaRepository.save(entity);
        return subscriptionEntityMapper.entityToDomain(savedEntity);
    }

    @Override
    public Optional<UserSubscription> findActiveByUserId(Long userId, Instant now) {
        subscriptionJpaRepository.updateExpiredSubscriptions(now);
        return subscriptionJpaRepository.findActiveSubscriptions(userId, SubscriptionStatus.ACTIVE, now)
                .stream()
                .findFirst()
                .map(subscriptionEntityMapper::entityToDomain);
    }

    @Override
    public boolean existsByPaymentOrderId(Long paymentOrderId) {
        return subscriptionJpaRepository.existsByPaymentOrderId(paymentOrderId);
    }
}
