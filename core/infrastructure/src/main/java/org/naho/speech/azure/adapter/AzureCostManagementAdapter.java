package org.naho.speech.azure.adapter;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.ApplicationException;
import org.naho.speech.azure.command.AzureCostQueryCommand;
import org.naho.speech.azure.config.AzureCostConfigProperties;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.azure.port.out.AzureCostManagementPort;
import org.naho.speech.azure.result.AzureCostChartResult;
import org.naho.speech.azure.result.AzureCostPointResult;
import org.naho.speech.azure.result.AzureCostSummaryResult;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class AzureCostManagementAdapter implements AzureCostManagementPort {

    private final ClientSecretCredential credential;
    private final AzureCostConfigProperties configProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AzureCostManagementAdapter(AzureCostConfigProperties configProperties) {
        this.configProperties = configProperties;
        this.credential = new ClientSecretCredentialBuilder()
                .tenantId(configProperties.getTenantId())
                .clientId(configProperties.getClientId())
                .clientSecret(configProperties.getClientSecret())
                .build();
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public AzureCostSummaryResult fetchCostSummary(String timeframe) {
        String token = getAccessToken();
        String url = String.format(configProperties.getQueryUrlTemplate(), configProperties.getSubscriptionId());

        Map<String, Object> body = new HashMap<>();
        body.put("type", "Usage");
        body.put("timeframe", timeframe != null ? timeframe : "MonthToDate");

        Map<String, Object> totalCostAgg = new HashMap<>();
        totalCostAgg.put("name", "PreTaxCost");
        totalCostAgg.put("function", "Sum");

        Map<String, Object> aggregation = new HashMap<>();
        aggregation.put("totalCost", totalCostAgg);

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("granularity", "None");
        dataset.put("aggregation", aggregation);
        body.put("dataset", dataset);

        try {
            JsonNode root = executePostQuery(url, token, body);
            JsonNode properties = root.path("properties");
            JsonNode columns = properties.path("columns");
            JsonNode rows = properties.path("rows");

            int costIndex = -1;
            int currencyIndex = -1;

            for (int i = 0; i < columns.size(); i++) {
                String colName = columns.get(i).path("name").asText();
                if ("PreTaxCost".equalsIgnoreCase(colName) || "totalCost".equalsIgnoreCase(colName)) {
                    costIndex = i;
                } else if ("Currency".equalsIgnoreCase(colName)) {
                    currencyIndex = i;
                }
            }

            BigDecimal totalCost = BigDecimal.ZERO;
            String currency = "USD";

            if (rows.isArray() && !rows.isEmpty()) {
                JsonNode firstRow = rows.get(0);
                if (costIndex != -1 && firstRow.has(costIndex) && !firstRow.get(costIndex).isNull()) {
                    totalCost = new BigDecimal(firstRow.get(costIndex).asText());
                }
                if (currencyIndex != -1 && firstRow.has(currencyIndex) && !firstRow.get(currencyIndex).isNull()) {
                    currency = firstRow.get(currencyIndex).asText();
                }
            }

            return new AzureCostSummaryResult(totalCost, currency, timeframe);

        } catch (Exception e) {
            log.error("Error fetching Azure cost summary: ", e);
            throw new ApplicationException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.AZURE_COST_API_FETCH_FAILED,
                    e.getMessage());
        }
    }

    @Override
    public AzureCostChartResult fetchCostChart(AzureCostQueryCommand command) {
        String token = getAccessToken();
        String url = String.format(configProperties.getQueryUrlTemplate(), configProperties.getSubscriptionId());

        Map<String, Object> body = new HashMap<>();
        body.put("type", "Usage");

        String timeframe = command.getTimeframe();
        if ("Custom".equalsIgnoreCase(timeframe) && command.getFromDate() != null && command.getToDate() != null) {
            body.put("timeframe", "Custom");
            Map<String, String> timePeriod = new HashMap<>();
            timePeriod.put("from", command.getFromDate().format(DateTimeFormatter.ISO_INSTANT));
            timePeriod.put("to", command.getToDate().format(DateTimeFormatter.ISO_INSTANT));
            body.put("timePeriod", timePeriod);
        } else {
            body.put("timeframe", timeframe != null ? timeframe : "MonthToDate");
        }

        Map<String, Object> totalCostAgg = new HashMap<>();
        totalCostAgg.put("name", "PreTaxCost");
        totalCostAgg.put("function", "Sum");

        Map<String, Object> aggregation = new HashMap<>();
        aggregation.put("totalCost", totalCostAgg);

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("granularity", command.getGranularity() != null ? command.getGranularity() : "Monthly");
        dataset.put("aggregation", aggregation);

        body.put("dataset", dataset);

        try {
            JsonNode root = executePostQuery(url, token, body);
            JsonNode properties = root.path("properties");
            JsonNode columns = properties.path("columns");
            JsonNode rows = properties.path("rows");

            int costIndex = -1;
            int dateIndex = -1;
            int currencyIndex = -1;

            for (int i = 0; i < columns.size(); i++) {
                String colName = columns.get(i).path("name").asText();
                if ("PreTaxCost".equalsIgnoreCase(colName) || "totalCost".equalsIgnoreCase(colName)) {
                    costIndex = i;
                } else if ("BillingMonth".equalsIgnoreCase(colName) || "UsageDate".equalsIgnoreCase(colName)
                        || "Date".equalsIgnoreCase(colName)) {
                    dateIndex = i;
                } else if ("Currency".equalsIgnoreCase(colName)) {
                    currencyIndex = i;
                }
            }

            BigDecimal totalCostAccumulated = BigDecimal.ZERO;
            String currency = "USD";
            List<AzureCostPointResult> points = new ArrayList<>();

            if (rows.isArray()) {
                for (JsonNode row : rows) {
                    BigDecimal cost = BigDecimal.ZERO;
                    String dateOrMonth = "";

                    if (costIndex != -1 && row.has(costIndex) && !row.get(costIndex).isNull()) {
                        cost = new BigDecimal(row.get(costIndex).asText());
                        totalCostAccumulated = totalCostAccumulated.add(cost);
                    }

                    if (dateIndex != -1 && row.has(dateIndex) && !row.get(dateIndex).isNull()) {
                        dateOrMonth = row.get(dateIndex).asText();
                    }

                    if (currencyIndex != -1 && row.has(currencyIndex) && !row.get(currencyIndex).isNull()) {
                        currency = row.get(currencyIndex).asText();
                    }

                    points.add(new AzureCostPointResult(dateOrMonth, cost, currency));
                }
            }

            return new AzureCostChartResult(
                    totalCostAccumulated,
                    currency,
                    command.getGranularity() != null ? command.getGranularity() : "Monthly",
                    points);

        } catch (Exception e) {
            log.error("Error fetching Azure cost chart data: ", e);
            throw new ApplicationException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.AZURE_COST_API_FETCH_FAILED,
                    e.getMessage());
        }
    }

    private String getAccessToken() {
        try {
            TokenRequestContext context = new TokenRequestContext().addScopes(configProperties.getScope());
            AccessToken accessToken = credential.getToken(context).block();
            if (accessToken == null || accessToken.getToken() == null) {
                throw new IllegalStateException("Acquired null token from Azure ClientSecretCredential");
            }
            return accessToken.getToken();
        } catch (Exception e) {
            log.error("Failed to acquire Azure OAuth2 access token", e);
            throw new ApplicationException(
                    AzureSpeechErrorCode.SPEECH_AZURE_SERVICE_ERROR,
                    SpeechDetailMessageKey.AZURE_COST_API_FETCH_FAILED,
                    e.getMessage());
        }
    }

    private JsonNode executePostQuery(String url, String token, Object bodyObj) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        String jsonBody = objectMapper.writeValueAsString(bodyObj);
        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return objectMapper.readTree(response.getBody());
        }
        throw new IllegalStateException("Azure Cost API returned status: " + response.getStatusCode());
    }
}
