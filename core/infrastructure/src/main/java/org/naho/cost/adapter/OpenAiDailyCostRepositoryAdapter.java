package org.naho.cost.adapter;

import lombok.RequiredArgsConstructor;
import org.naho.cost.entity.OpenAiDailyCostEntity;
import org.naho.cost.mapper.OpenAiDailyCostMapper;
import org.naho.cost.model.OpenAiDailyCost;
import org.naho.cost.repository.OpenAiDailyCostJpaRepository;
import org.naho.speech.llm.port.out.OpenAiCostRepositoryPort;
import org.naho.speech.llm.result.OpenAiCostPointResult;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OpenAiDailyCostRepositoryAdapter implements OpenAiCostRepositoryPort {

    private final OpenAiDailyCostJpaRepository repository;
    private final OpenAiDailyCostMapper mapper;

    @Override
    @Transactional
    public OpenAiDailyCost saveOrUpdate(OpenAiDailyCost dailyCost) {
        if (dailyCost == null || dailyCost.getRecordDate() == null) {
            return null;
        }
        OpenAiDailyCostEntity entity = repository.findByRecordDate(dailyCost.getRecordDate())
                .orElseGet(() -> mapper.toEntity(dailyCost));

        entity.setCostAmount(dailyCost.getCostAmount());
        entity.setCurrency(dailyCost.getCurrency() != null ? dailyCost.getCurrency() : "USD");

        OpenAiDailyCostEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void saveAll(List<OpenAiDailyCost> dailyCosts) {
        if (dailyCosts == null || dailyCosts.isEmpty()) {
            return;
        }
        for (OpenAiDailyCost dailyCost : dailyCosts) {
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
    public List<OpenAiDailyCost> findDailyCostsBetween(LocalDate fromDate, LocalDate toDate) {
        List<OpenAiDailyCostEntity> entities = repository.findByRecordDateBetweenOrderByRecordDateAsc(fromDate, toDate);
        return mapper.toDomainList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OpenAiCostPointResult> findMonthlyCostsSummary(LocalDate startDate) {
        List<OpenAiDailyCostJpaRepository.MonthlyCostProjection> projections = repository.getMonthlyCostsSummary(startDate);
        List<OpenAiCostPointResult> results = new ArrayList<>();
        for (OpenAiDailyCostJpaRepository.MonthlyCostProjection proj : projections) {
            results.add(new OpenAiCostPointResult(
                    proj.getDateOrMonth(),
                    proj.getCost() != null ? proj.getCost() : BigDecimal.ZERO,
                    proj.getCurrency() != null ? proj.getCurrency() : "USD"
            ));
        }
        return results;
    }
}
