package org.naho.subscription.usecase;

import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.SubscriptionStatus;

import java.time.Instant;
import java.util.Optional;

public class GetActiveSubscriptionUseCase implements GetActiveSubscriptionInputPort {

    private final SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort;
    private final SubscriptionPlanResultMapper subscriptionPlanResultMapper;

    public GetActiveSubscriptionUseCase(
            SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort,
            SubscriptionPlanResultMapper subscriptionPlanResultMapper
    ) {
        this.subscriptionPlanRepositoryPort = subscriptionPlanRepositoryPort;
        this.subscriptionPlanResultMapper = subscriptionPlanResultMapper;
    }

    /**
     * Lấy ra gói đăng kí hiện tại của người dùng
     *
     * @param userId user id (lấy từ JWT)
     * @return SubscriptionPlanResult
     */
    @Override
    public SubscriptionPlanResult getUserActiveSubscriptionPlan(Long userId) {
        Instant now = Instant.now();

        // Lấy gói đăng kí hiện tại của người dùng
        Optional<SubscriptionPlan> subscriptionPlan = subscriptionPlanRepositoryPort
                .findCurrentSubscriptionPlanByUserIdAndStatus(
                        userId,
                        SubscriptionStatus.ACTIVE,
                        PlanStatus.ACTIVE,
                        now
                );

        // nếu có thì trả về
        if (subscriptionPlan.isPresent()) {
            return subscriptionPlanResultMapper.mapToPlanResult(subscriptionPlan.get());
        }

        // nếu không có thì trả về gói FREE
        return subscriptionPlanRepositoryPort.findByCode(PlanCode.FREE)
                .map(subscriptionPlanResultMapper::mapToPlanResult)
                .orElseThrow(() -> new ApplicationException(
                        SubscriptionErrorCode.PLAN_NOT_FOUND,
                        SubscriptionDetailMessageKey.PLAN_NOT_FOUND
                ));
    }
}
