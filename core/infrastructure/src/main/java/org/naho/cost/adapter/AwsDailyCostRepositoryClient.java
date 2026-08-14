package org.naho.cost.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.cost.constant.CostProperties;
import org.naho.cost.entity.AwsDailyCostEntity;
import org.naho.cost.mapper.AwsDailyCostMapper;
import org.naho.cost.model.AwsDailyCost;
import org.naho.cost.port.out.AwsCostRepositoryPort;
import org.naho.cost.repository.AwsDailyCostJpaRepository;
import org.naho.cost.result.AwsCostPointResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AwsDailyCostRepositoryClient implements AwsCostRepositoryPort {

    private final AwsDailyCostJpaRepository awsDailyCostJpaRepository;
    private final AwsDailyCostMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<BigDecimal> sumCostBetween(LocalDate startDate, LocalDate endDate) {
        return awsDailyCostJpaRepository.sumCostBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AwsDailyCost> findDailyCostsBetween(LocalDate fromDate, LocalDate toDate) {
        List<AwsDailyCostEntity> entities = awsDailyCostJpaRepository
                .findByRecordDateBetweenOrderByRecordDateAsc(fromDate, toDate);
        return mapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AwsCostPointResult> findMonthlyCostsSummary(LocalDate startDate) {
        List<AwsDailyCostJpaRepository.MonthlyCostProjection> projections = awsDailyCostJpaRepository
                .getMonthlyCostsSummary(startDate);
        List<AwsCostPointResult> results = new ArrayList<>();
        for (AwsDailyCostJpaRepository.MonthlyCostProjection proj : projections) {
            results.add(new AwsCostPointResult(
                    proj.getDateOrMonth(),
                    proj.getCost() != null ? proj.getCost() : BigDecimal.ZERO,
                    proj.getCurrency() != null ? proj.getCurrency() : CostProperties.USD_CURRENCY));
        }
        return results;
    }
}
