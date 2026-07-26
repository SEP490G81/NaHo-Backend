package org.naho.subscription.usecase;

import org.naho.subscription.mapper.SubscriptionPlanResultMapper;
import org.naho.subscription.model.SubscriptionPlan;
import org.naho.subscription.port.in.ListActivePlansInputPort;
import org.naho.subscription.port.out.SubscriptionPlanRepositoryPort;
import org.naho.subscription.result.SubscriptionPlanResult;

import java.util.List;

public class ListActivePlansUseCase implements ListActivePlansInputPort {

    private final SubscriptionPlanRepositoryPort planRepositoryPort;
    private final SubscriptionPlanResultMapper planResultMapper;

    public ListActivePlansUseCase(SubscriptionPlanRepositoryPort planRepositoryPort,
                                  SubscriptionPlanResultMapper planResultMapper) {
        this.planRepositoryPort = planRepositoryPort;
        this.planResultMapper = planResultMapper;
    }

    public ListActivePlansUseCase(SubscriptionPlanRepositoryPort planRepositoryPort) {
        this(planRepositoryPort, new SubscriptionPlanResultMapper());
    }

    @Override
    public List<SubscriptionPlanResult> listActivePlans() {
        List<SubscriptionPlan> plans = planRepositoryPort.findAllActive();
        return plans.stream()
                .map(planResultMapper::mapToPlanResult)
                .toList();
    }
}
