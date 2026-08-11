package org.naho.subscription.usecase;

import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.mapper.UserSubscriptionResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.in.GetActiveSubscriptionInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanStatus;
import org.naho.subscription.type.SubscriptionStatus;

import java.time.Instant;
import java.util.Optional;

public class GetActiveSubscriptionUseCase implements GetActiveSubscriptionInputPort {

        private final SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort;
        private final SubscriptionPlanResultMapper subscriptionPlanResultMapper;
        private final UserSubscriptionResultMapper userSubscriptionResultMapper;
        private final UserSubscriptionRepositoryPort userSubscriptionRepositoryPort;

        public GetActiveSubscriptionUseCase(
                        SubscriptionPlanRepositoryPort subscriptionPlanRepositoryPort,
                        SubscriptionPlanResultMapper subscriptionPlanResultMapper,
                        UserSubscriptionResultMapper userSubscriptionResultMapper,
                        UserSubscriptionRepositoryPort userSubscriptionRepositoryPort) {
                this.subscriptionPlanRepositoryPort = subscriptionPlanRepositoryPort;
                this.subscriptionPlanResultMapper = subscriptionPlanResultMapper;
                this.userSubscriptionRepositoryPort = userSubscriptionRepositoryPort;
                this.userSubscriptionResultMapper = userSubscriptionResultMapper;
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
                                                now);

                // nếu có thì trả về
                if (subscriptionPlan.isPresent()) {
                        return subscriptionPlanResultMapper.mapToPlanResult(subscriptionPlan.get());
                }

                // nếu không có thì trả về gói FREE
                return subscriptionPlanRepositoryPort.findByCode(PlanCode.FREE)
                                .map(subscriptionPlanResultMapper::mapToPlanResult)
                                .orElseThrow(() -> new ApplicationException(
                                                SubscriptionErrorCode.PLAN_NOT_FOUND,
                                                SubscriptionDetailMessageKey.PLAN_NOT_FOUND));
        }

        @Override
        public UserSubscriptionResult getUserActiveSubscription(Long userId) {
                Instant now = Instant.now();

                // Lấy thông tin gói đăng kí hiện tại của người dùng
                SubscriptionPlanResult planResult = getUserActiveSubscriptionPlan(userId);

                // Lấy bản ghi đăng kí gói dịch vụ đang active của người dùng
                Optional<UserSubscription> userSubscription = userSubscriptionRepositoryPort
                                .findActiveByUserId(userId, now);

                if (userSubscription.isPresent()) {
                        return userSubscriptionResultMapper.mapToUserSubscriptionResult(userSubscription.get(),
                                        planResult);
                }

                // Trường hợp người dùng dùng gói FREE mặc định (chưa có bản ghi
                // UserSubscription)
                return new UserSubscriptionResult(
                                null,
                                userId,
                                planResult.id(),
                                null,
                                SubscriptionStatus.ACTIVE,
                                null,
                                null,
                                planResult);
        }
}