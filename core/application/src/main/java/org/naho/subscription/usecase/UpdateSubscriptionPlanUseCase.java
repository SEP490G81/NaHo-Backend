package org.naho.subscription.usecase;

import org.naho.i18n.message.subscription.SubscriptionDetailMessageKey;
import org.naho.i18n.message.user.UserDetailMessageKey;
import org.naho.payment.model.Money;
import org.naho.shared.exception.ApplicationException;
import org.naho.shared.port.out.TransactionPort;
import org.naho.subscription.command.UpdateSubscriptionPlanCommand;
import org.naho.subscription.exception.SubscriptionErrorCode;
import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.in.UpdateSubscriptionPlanInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;
import org.naho.user.exception.UserErrorCode;
import org.naho.user.model.Role;
import org.naho.user.port.out.RoleRepositoryPort;
import org.naho.user.type.RoleName;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

public class UpdateSubscriptionPlanUseCase implements UpdateSubscriptionPlanInputPort {

    private final RoleRepositoryPort roleRepositoryPort;
    private final SubscriptionPlanRepositoryPort planRepositoryPort;
    private final SubscriptionPlanResultMapper planResultMapper;
    private final TransactionPort transactionPort;

    public UpdateSubscriptionPlanUseCase(
            RoleRepositoryPort roleRepositoryPort,
            SubscriptionPlanRepositoryPort planRepositoryPort,
            SubscriptionPlanResultMapper planResultMapper,
            TransactionPort transactionPort) {
        this.roleRepositoryPort = roleRepositoryPort;
        this.planRepositoryPort = planRepositoryPort;
        this.planResultMapper = planResultMapper;
        this.transactionPort = transactionPort;
    }

    public UpdateSubscriptionPlanUseCase(
            RoleRepositoryPort roleRepositoryPort,
            SubscriptionPlanRepositoryPort planRepositoryPort,
            TransactionPort transactionPort) {
        this(roleRepositoryPort, planRepositoryPort, new SubscriptionPlanResultMapper(), transactionPort);
    }

    @Override
    public SubscriptionPlanResult updateSubscriptionPlan(UpdateSubscriptionPlanCommand command) {
        return transactionPort.execute(() -> doUpdateSubscriptionPlan(command));
    }

    private SubscriptionPlanResult doUpdateSubscriptionPlan(UpdateSubscriptionPlanCommand command) {
        // 1. Verify Admin Role
        List<String> roleNames = roleRepositoryPort.findRoleNamesByUserId(command.adminUserId());
        boolean isAdmin = roleNames.contains(RoleName.ADMIN.name());
        if (!isAdmin) {
            Optional<Role> roleOpt = roleRepositoryPort.findByUserId(command.adminUserId());
            isAdmin = roleOpt.isPresent() && RoleName.ADMIN.equals(roleOpt.get().getRoleName());
        }
        if (!isAdmin) {
            throw new ApplicationException(UserErrorCode.USER_ACCESS_DENIED, UserDetailMessageKey.USER_ACCESS_DENIED);
        }

        // 2. Find Subscription Plan
        SubscriptionPlan plan = planRepositoryPort.findById(command.planId())
                .orElseThrow(() -> new ApplicationException(
                        SubscriptionErrorCode.PLAN_NOT_FOUND,
                        SubscriptionDetailMessageKey.PLAN_NOT_FOUND));

        // 3. Create Money object if priceAmount or priceCurrency is provided
        Money price = null;
        if (command.priceAmount() != null) {
            String currencyCode = (command.priceCurrency() != null && !command.priceCurrency().isBlank())
                    ? command.priceCurrency()
                    : (plan.getPrice() != null ? plan.getPrice().currency().getCurrencyCode() : "VND");
            price = new Money(command.priceAmount(), Currency.getInstance(currencyCode));
        } else if (command.priceCurrency() != null && !command.priceCurrency().isBlank() && plan.getPrice() != null) {
            price = new Money(plan.getPrice().amount(), Currency.getInstance(command.priceCurrency()));
        }

        // 4. Update details on domain model
        plan.updateDetails(
                command.description(),
                command.tier(),
                price,
                command.durationDays(),
                command.dailySpeakingQuestionEvaluationLimit(),
                command.maxSpeakingQuestionRecordingSeconds(),
                command.maxTurnsPerAiSession(),
                command.dailyAiSessionStartLimit(),
                command.maxAiTurnSpeakingSeconds(),
                command.maxInProgressSessionCount(),
                command.sampleAnswerEnabled(),
                command.status()
        );

        // 5. Save plan
        SubscriptionPlan updatedPlan = planRepositoryPort.save(plan);

        // 6. Map to Result
        return planResultMapper.mapToPlanResult(updatedPlan);
    }
}
