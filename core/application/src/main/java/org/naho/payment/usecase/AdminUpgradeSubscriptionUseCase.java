package org.naho.payment.usecase;

import org.naho.i18n.message.payment.PaymentDetailMessageKey;
import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.payment.command.AdminUpgradeSubscriptionCommand;
import org.naho.payment.exception.PaymentErrorCode;
import org.naho.payment.port.in.AdminUpgradeSubscriptionInputPort;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.mapper.UserSubscriptionResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.model.UserSubscription;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.port.out.UserSubscriptionRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.subscription.result.UserSubscriptionResult;
import org.naho.subscription.type.PlanCode;
import org.naho.subscription.type.PlanTier;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.port.out.UserRepositoryPort;
import org.naho.user.type.RoleName;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public class AdminUpgradeSubscriptionUseCase implements AdminUpgradeSubscriptionInputPort {

    private final RoleRepositoryPort roleRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final SubscriptionPlanRepositoryPort planRepositoryPort;
    private final UserSubscriptionRepositoryPort subscriptionRepositoryPort;
    private final SubscriptionPlanResultMapper planResultMapper;
    private final UserSubscriptionResultMapper userSubscriptionResultMapper;
    private final org.naho.shared.port.out.EventPublisherPort eventPublisherPort;
    private final TransactionPort transactionPort;

    public AdminUpgradeSubscriptionUseCase(RoleRepositoryPort roleRepositoryPort,
                                           UserRepositoryPort userRepositoryPort,
                                           SubscriptionPlanRepositoryPort planRepositoryPort,
                                           UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                                           SubscriptionPlanResultMapper planResultMapper,
                                           UserSubscriptionResultMapper userSubscriptionResultMapper,
                                           org.naho.shared.port.out.EventPublisherPort eventPublisherPort,
                                           TransactionPort transactionPort) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.userRepositoryPort = userRepositoryPort;
        this.planRepositoryPort = planRepositoryPort;
        this.subscriptionRepositoryPort = subscriptionRepositoryPort;
        this.planResultMapper = planResultMapper;
        this.userSubscriptionResultMapper = userSubscriptionResultMapper;
        this.eventPublisherPort = eventPublisherPort;
        this.transactionPort = transactionPort;
    }

    public AdminUpgradeSubscriptionUseCase(RoleRepositoryPort roleRepositoryPort,
                                           UserRepositoryPort userRepositoryPort,
                                           SubscriptionPlanRepositoryPort planRepositoryPort,
                                           UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                                           SubscriptionPlanResultMapper planResultMapper,
                                           org.naho.shared.port.out.EventPublisherPort eventPublisherPort,
                                           TransactionPort transactionPort) {
        this(roleRepositoryPort, userRepositoryPort, planRepositoryPort, subscriptionRepositoryPort, planResultMapper, new UserSubscriptionResultMapper(), eventPublisherPort, transactionPort);
    }

    public AdminUpgradeSubscriptionUseCase(RoleRepositoryPort roleRepositoryPort,
                                           UserRepositoryPort userRepositoryPort,
                                           SubscriptionPlanRepositoryPort planRepositoryPort,
                                           UserSubscriptionRepositoryPort subscriptionRepositoryPort,
                                           org.naho.shared.port.out.EventPublisherPort eventPublisherPort,
                                           TransactionPort transactionPort) {
        this(roleRepositoryPort, userRepositoryPort, planRepositoryPort, subscriptionRepositoryPort, new SubscriptionPlanResultMapper(), new UserSubscriptionResultMapper(), eventPublisherPort, transactionPort);
    }

    @Override
    public UserSubscriptionResult upgradeSubscription(AdminUpgradeSubscriptionCommand command) {
        return transactionPort.execute(() -> doUpgradeSubscription(command));
    }

    private UserSubscriptionResult doUpgradeSubscription(AdminUpgradeSubscriptionCommand command) {
        Instant now = Instant.now();

        // 1. Verify Admin Role
        List<String> adminRoles = roleRepositoryPort.findRoleNamesByUserId(command.adminUserId());
        if (adminRoles == null || !adminRoles.contains(RoleName.ADMIN.name())) {
            throw new ApplicationException(UserErrorCode.USER_ACCESS_DENIED, UserDetailMessageKey.USER_ACCESS_DENIED);
        }

        // 2. Verify Target User Exists
        userRepositoryPort.findById(command.targetUserId())
                .orElseThrow(() -> new ApplicationException(UserErrorCode.USER_NOT_FOUND, UserDetailMessageKey.USER_NOT_FOUND));

        // 3. Verify Target Plan Exists and is Active
        SubscriptionPlan targetPlan = planRepositoryPort.findActiveByCode(command.planCode())
                .orElseThrow(() -> new ApplicationException(PaymentErrorCode.PLAN_NOT_FOUND, SubscriptionDetailMessageKey.PLAN_NOT_FOUND));

        // 4. Get Current Active Subscription or Fallback to FREE plan info
        Optional<UserSubscription> currentActiveSubOpt = subscriptionRepositoryPort.findActiveByUserId(command.targetUserId(), now);
        SubscriptionPlan currentPlan;

        if (currentActiveSubOpt.isPresent()) {
            UserSubscription currentSub = currentActiveSubOpt.get();
            currentPlan = planRepositoryPort.findById(currentSub.getSubscriptionPlanId())
                    .orElseGet(() -> planRepositoryPort.findActiveByCode(PlanCode.FREE).orElse(null));
        } else {
            currentPlan = planRepositoryPort.findActiveByCode(PlanCode.FREE).orElse(null);
        }

        // 5. Tier Validations
        if (currentPlan != null) {
            PlanTier currentTier = currentPlan.getTier();
            PlanTier targetTier = targetPlan.getTier();

            if (currentTier == PlanTier.PREMIUM) {
                throw new ApplicationException(
                        PaymentErrorCode.MAXIMUM_SUBSCRIPTION_TIER_REACHED,
                        PaymentDetailMessageKey.PAYMENT_SUBSCRIPTION_MAX_TIER_REACHED);
            }

            if (targetTier.getLevel() <= currentTier.getLevel()) {
                throw new ApplicationException(
                        PaymentErrorCode.CANNOT_UPGRADE_SAME_OR_LOWER_TIER,
                        PaymentDetailMessageKey.PAYMENT_SUBSCRIPTION_SAME_OR_LOWER_TIER);
            }
        }

        // 6. Cancel previous active subscription if exists
        if (currentActiveSubOpt.isPresent()) {
            UserSubscription previousSub = currentActiveSubOpt.get();
            previousSub.cancel(now);
            subscriptionRepositoryPort.save(previousSub);
        }

        // 7. Create & Save New Subscription
        int durationDays = (command.customDurationDays() != null && command.customDurationDays() > 0)
                ? command.customDurationDays()
                : targetPlan.getDurationDays();

        UserSubscription newSubscription = UserSubscription.activate(
                command.targetUserId(),
                targetPlan.getId(),
                null,
                durationDays,
                now
        );

        UserSubscription savedSubscription = subscriptionRepositoryPort.save(newSubscription);
        SubscriptionPlanResult planResult = planResultMapper.mapToPlanResult(targetPlan);

        // Publish Event Nâng cấp gói bởi Admin
        if (eventPublisherPort != null) {
            eventPublisherPort.publish(new org.naho.user.event.UserPlanUpgradedEvent(
                    command.targetUserId(),
                    targetPlan.getCode().name()
            ));
        }

        return userSubscriptionResultMapper.mapToUserSubscriptionResult(savedSubscription, planResult);
    }
}
