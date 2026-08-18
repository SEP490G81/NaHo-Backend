package org.naho.cost.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiCostApiClient {

    private static final String OPENAI_COSTS_URL = "https://api.openai.com/v1/organization/costs";

    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    public List<FetchedDailyCost> fetchDailyCosts(String apiKey, LocalDate startDate, LocalDate endDate) {
        if (apiKey == null || apiKey.isBlank()) {
            return Collections.emptyList();
        }

        long startTime = startDate.atStartOfDay(ZoneOffset.UTC).toEpochSecond();
        long endTime = endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        Map<LocalDate, BigDecimal> dailyCostMap = new HashMap<>();
        String nextPage = null;
        boolean hasMore = true;

        while (hasMore) {
            try {
                StringBuilder urlBuilder = new StringBuilder(
                        String.format("%s?start_time=%d&end_time=%d&bucket_width=1d&limit=31&group_by=line_item", OPENAI_COSTS_URL, startTime, endTime)
                );
                if (nextPage != null && !nextPage.isBlank()) {
                    urlBuilder.append("&page=").append(nextPage);
                }

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(urlBuilder.toString()))
                        .header("Authorization", "Bearer " + apiKey)
                        .header("Content-Type", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonNode root = mapper.readTree(response.body());
                    hasMore = root.path("has_more").asBoolean(false);
                    nextPage = root.path("next_page").asText(null);

                    JsonNode dataNode = root.path("data");
                    if (dataNode.isArray()) {
                        for (JsonNode bucketOrItem : dataNode) {
                            long timestamp = bucketOrItem.path("start_time").asLong(0);
                            LocalDate date = timestamp > 0
                                    ? LocalDate.ofInstant(java.time.Instant.ofEpochSecond(timestamp), ZoneOffset.UTC)
                                    : LocalDate.now(ZoneOffset.UTC);

                            JsonNode resultsNode = bucketOrItem.path("results");
                            List<JsonNode> itemsToProcess = new ArrayList<>();
                            if (resultsNode.isArray() && !resultsNode.isEmpty()) {
                                for (JsonNode res : resultsNode) {
                                    itemsToProcess.add(res);
                                }
                            } else if (!bucketOrItem.has("results") && bucketOrItem.has("amount")) {
                                itemsToProcess.add(bucketOrItem);
                            }

                            for (JsonNode item : itemsToProcess) {
                                BigDecimal amount = BigDecimal.valueOf(item.path("amount").path("value").asDouble(0.0));
                                dailyCostMap.merge(date, amount, BigDecimal::add);
                            }
                        }
                    }

                    if (nextPage == null || nextPage.isBlank()) {
                        hasMore = false;
                    }
                } else {
                    throw new InfrastructureException(
                            LlmApplicationError.LLM_API_ERROR,
                            LlmDetailMessageKey.LLM_API_ERROR,
                            "OpenAI Costs API status: " + response.statusCode() + " | " + response.body()
                    );
                }
            } catch (InfrastructureException ie) {
                throw ie;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new InfrastructureException(
                        LlmApplicationError.LLM_CONNECTION_TIMEOUT,
                        LlmDetailMessageKey.LLM_CONNECTION_TIMEOUT,
                        e.getMessage()
                );
            } catch (Exception e) {
                throw new InfrastructureException(
                        LlmApplicationError.LLM_API_ERROR,
                        LlmDetailMessageKey.LLM_API_ERROR,
                        e.getMessage()
                );
            }
        }

        List<FetchedDailyCost> results = new ArrayList<>();
        for (Map.Entry<LocalDate, BigDecimal> entry : dailyCostMap.entrySet()) {
            results.add(FetchedDailyCost.builder()
                    .recordDate(entry.getKey())
                    .costAmount(entry.getValue().setScale(6, RoundingMode.HALF_UP))
                    .build());
        }
        return results;
    }

    @Getter
    @Builder
    public static class FetchedDailyCost {
        LocalDate recordDate;
        BigDecimal costAmount;
    }
}
