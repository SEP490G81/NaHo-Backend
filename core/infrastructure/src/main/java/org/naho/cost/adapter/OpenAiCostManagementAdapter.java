package org.naho.cost.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.cost.model.OpenAiDailyCost;
import org.naho.cost.port.out.OpenAiCostManagementPort;
import org.naho.speech.llm.constant.OpenAiConfigProperties;
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
        String adminKey = configProperties.getAdminApiKey();
        if (adminKey != null && !adminKey.isBlank() && !adminKey.startsWith("${")) {
            return adminKey;
        }
        String apiKey = configProperties.getApiKey();
        if (apiKey != null && !apiKey.startsWith("${")) {
            return apiKey;
        }
        return apiKey;
    }
}
