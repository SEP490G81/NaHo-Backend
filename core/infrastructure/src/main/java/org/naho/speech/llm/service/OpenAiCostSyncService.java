package org.naho.speech.llm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.speech.llm.adapter.OpenAiCostApiClient;
import org.naho.speech.llm.constant.OpenAiConfigProperties;
import org.naho.speech.llm.entity.OpenAiDailyCostEntity;
import org.naho.speech.llm.repository.OpenAiDailyCostJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiCostSyncService {

    private final OpenAiConfigProperties properties;
    private final OpenAiCostApiClient apiClient;
    private final OpenAiDailyCostJpaRepository repository;

    @Transactional
    public void syncDateRange(LocalDate startDate, LocalDate endDate) {
        String apiKey = getEffectiveApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Cannot sync OpenAI costs: API Key is missing.");
            return;
        }

        List<OpenAiCostApiClient.FetchedDailyCost> fetchedDataList = apiClient.fetchDailyCosts(apiKey, startDate, endDate);

        if (fetchedDataList.isEmpty()) {
            return;
        }

        for (var data : fetchedDataList) {
            Optional<OpenAiDailyCostEntity> existing = repository.findByRecordDate(data.getRecordDate());

            OpenAiDailyCostEntity entity;
            if (existing.isPresent()) {
                entity = existing.get();
                entity.setCostAmount(data.getCostAmount());
            } else {
                entity = OpenAiDailyCostEntity.builder()
                        .recordDate(data.getRecordDate())
                        .costAmount(data.getCostAmount())
                        .currency("USD")
                        .build();
            }
            repository.save(entity);
        }
    }

    private String getEffectiveApiKey() {
        if (properties.getAdminApiKey() != null && !properties.getAdminApiKey().isBlank()) {
            return properties.getAdminApiKey();
        }
        return properties.getApiKey();
    }
}
