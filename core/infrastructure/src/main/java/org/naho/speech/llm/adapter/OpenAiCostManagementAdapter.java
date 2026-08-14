package org.naho.speech.llm.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.speech.llm.constant.OpenAiConfigProperties;
import org.naho.speech.llm.model.OpenAiDailyCost;
import org.naho.speech.llm.port.out.OpenAiCostManagementPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiCostManagementAdapter implements OpenAiCostManagementPort {

    private final OpenAiConfigProperties configProperties;
    private final OpenAiCostApiClient apiClient;

    @Override
    public List<OpenAiDailyCost> fetchDailyCostsFromOpenAi(LocalDate fromDate, LocalDate toDate) {
        String apiKey = getEffectiveApiKey();
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Cannot fetch OpenAI costs: API Key is missing.");
            return Collections.emptyList();
        }

        try {
            List<OpenAiCostApiClient.FetchedDailyCost> fetched = apiClient.fetchDailyCosts(apiKey, fromDate, toDate);
            if (fetched == null || fetched.isEmpty()) {
                return Collections.emptyList();
            }

            List<OpenAiDailyCost> result = new ArrayList<>();
            for (var item : fetched) {
                result.add(OpenAiDailyCost.builder()
                        .recordDate(item.getRecordDate())
                        .costAmount(item.getCostAmount())
                        .currency("USD")
                        .build());
            }
            return result;

        } catch (Exception e) {
            log.error("Error fetching OpenAI daily costs from API: ", e);
            return Collections.emptyList();
        }
    }

    private String getEffectiveApiKey() {
        if (configProperties.getAdminApiKey() != null && !configProperties.getAdminApiKey().isBlank()) {
            return configProperties.getAdminApiKey();
        }
        return configProperties.getApiKey();
    }
}
