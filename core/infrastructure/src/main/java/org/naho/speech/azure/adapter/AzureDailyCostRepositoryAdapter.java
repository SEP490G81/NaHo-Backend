package org.naho.speech.azure.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.speech.azure.entity.AzureDailyCostEntity;
import org.naho.speech.azure.mapper.AzureDailyCostMapper;
import org.naho.speech.azure.model.AzureDailyCost;
import org.naho.speech.azure.port.out.AzureCostRepositoryPort;
import org.naho.speech.azure.repository.AzureDailyCostJpaRepository;
import org.naho.speech.azure.result.AzureCostPointResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AzureDailyCostRepositoryAdapter implements AzureCostRepositoryPort {

    private final AzureDailyCostJpaRepository repository;
    private final AzureDailyCostMapper mapper;

    @Override
    @Transactional
    public AzureDailyCost saveOrUpdate(AzureDailyCost dailyCost) {
        if (dailyCost == null || dailyCost.getRecordDate() == null) {
            return null;
        }
        AzureDailyCostEntity entity = repository.findByRecordDate(dailyCost.getRecordDate())
                .orElseGet(() -> mapper.toEntity(dailyCost));

        entity.setCostAmount(dailyCost.getCostAmount());
        entity.setCurrency(dailyCost.getCurrency() != null ? dailyCost.getCurrency() : "USD");

        AzureDailyCostEntity saved = repository.save(entity);
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
        return repository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BigDecimal> sumCostBetween(LocalDate startDate, LocalDate endDate) {
        return repository.sumCostBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AzureDailyCost> findDailyCostsBetween(LocalDate fromDate, LocalDate toDate) {
        List<AzureDailyCostEntity> entities = repository.findByRecordDateBetweenOrderByRecordDateAsc(fromDate, toDate);
        return mapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AzureCostPointResult> findMonthlyCostsSummary(LocalDate startDate) {
        List<AzureDailyCostJpaRepository.MonthlyCostProjection> projections = repository.getMonthlyCostsSummary(startDate);
        List<AzureCostPointResult> results = new ArrayList<>();
        for (AzureDailyCostJpaRepository.MonthlyCostProjection proj : projections) {
            results.add(new AzureCostPointResult(
                    proj.getDateOrMonth(),
                    proj.getCost() != null ? proj.getCost() : BigDecimal.ZERO,
                    proj.getCurrency() != null ? proj.getCurrency() : "USD"
            ));
        }
        return results;
    }
}
