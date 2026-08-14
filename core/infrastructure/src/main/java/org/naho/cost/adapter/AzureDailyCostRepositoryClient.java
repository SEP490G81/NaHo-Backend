package org.naho.cost.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.cost.constant.CostProperties;
import org.naho.cost.entity.AzureDailyCostEntity;
import org.naho.cost.mapper.AzureDailyCostMapper;
import org.naho.cost.model.AzureDailyCost;
import org.naho.cost.port.out.AzureCostRepositoryPort;
import org.naho.cost.repository.AzureDailyCostJpaRepository;
import org.naho.cost.result.AzureCostPointResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AzureDailyCostRepositoryClient implements AzureCostRepositoryPort {

    private final AzureDailyCostJpaRepository azureDailyCostJpaRepository;
    private final AzureDailyCostMapper mapper;

    @Override
    @Transactional
    public AzureDailyCost saveOrUpdate(AzureDailyCost dailyCost) {
        if (dailyCost == null || dailyCost.getRecordDate() == null) {
            return null;
        }
        AzureDailyCostEntity entity = azureDailyCostJpaRepository.findByRecordDate(dailyCost.getRecordDate())
                .orElseGet(() -> mapper.toEntity(dailyCost));

        entity.setCostAmount(dailyCost.getCostAmount());
        entity.setCurrency(dailyCost.getCurrency() != null ? dailyCost.getCurrency() : CostProperties.USD_CURRENCY);

        AzureDailyCostEntity saved = azureDailyCostJpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void saveAll(List<AzureDailyCost> dailyCosts) {
        if (dailyCosts == null || dailyCosts.isEmpty()) {
            return;
        }
        for (AzureDailyCost dailyCost : dailyCosts) {
            saveOrUpdate(dailyCost);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return azureDailyCostJpaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BigDecimal> sumCostBetween(LocalDate startDate, LocalDate endDate) {
        return azureDailyCostJpaRepository.sumCostBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AzureDailyCost> findDailyCostsBetween(LocalDate fromDate, LocalDate toDate) {
        List<AzureDailyCostEntity> entities = azureDailyCostJpaRepository.findByRecordDateBetweenOrderByRecordDateAsc(fromDate, toDate);
        return mapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AzureCostPointResult> findMonthlyCostsSummary(LocalDate startDate) {
        List<AzureDailyCostJpaRepository.MonthlyCostProjection> projections = azureDailyCostJpaRepository.getMonthlyCostsSummary(startDate);
        List<AzureCostPointResult> results = new ArrayList<>();
        for (AzureDailyCostJpaRepository.MonthlyCostProjection proj : projections) {
            results.add(new AzureCostPointResult(
                    proj.getDateOrMonth(),
                    proj.getCost() != null ? proj.getCost() : BigDecimal.ZERO,
                    proj.getCurrency() != null ? proj.getCurrency() : CostProperties.USD_CURRENCY
            ));
        }
        return results;
    }
}
