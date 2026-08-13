package org.naho.speech.azure.adapter;

import com.azure.core.credential.AccessToken;
import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.naho.i18n.message.speech.SpeechDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.azure.config.AzureCostConfigProperties;
import org.naho.speech.azure.exception.AzureSpeechErrorCode;
import org.naho.speech.azure.model.AzureDailyCost;
import org.naho.speech.azure.port.out.AzureCostManagementPort;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
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
    public List<AzureDailyCost> fetchDailyCostsFromAzure(LocalDate fromDate, LocalDate toDate) {
        String token = getAccessToken();
        String url = String.format(configProperties.getQueryUrlTemplate(), configProperties.getSubscriptionId());

        Map<String, Object> body = new HashMap<>();
        body.put("type", "Usage");
        body.put("timeframe", "Custom");

        Map<String, String> timePeriod = new HashMap<>();
        timePeriod.put("from", fromDate.atStartOfDay(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
        timePeriod.put("to", toDate.atTime(23, 59, 59).atZone(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT));
        body.put("timePeriod", timePeriod);

        Map<String, Object> totalCostAgg = new HashMap<>();
        totalCostAgg.put("name", "PreTaxCost");
        totalCostAgg.put("function", "Sum");

        Map<String, Object> aggregation = new HashMap<>();
        aggregation.put("totalCost", totalCostAgg);

        Map<String, Object> dataset = new HashMap<>();
        dataset.put("granularity", "Daily");
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
                if ("PreTaxCost".equalsIgnoreCase(colName) || "totalCost".equalsIgnoreCase(colName) || "Cost".equalsIgnoreCase(colName)) {
                    costIndex = i;
                } else if ("UsageDate".equalsIgnoreCase(colName) || "Date".equalsIgnoreCase(colName) || "BillingMonth".equalsIgnoreCase(colName)) {
                    dateIndex = i;
                } else if ("Currency".equalsIgnoreCase(colName)) {
                    currencyIndex = i;
                }
            }

            List<AzureDailyCost> list = new ArrayList<>();
            if (rows.isArray()) {
                for (JsonNode row : rows) {
                    BigDecimal cost = BigDecimal.ZERO;
                    LocalDate usageDate = null;
                    String currency = "USD";

                    if (costIndex != -1 && row.has(costIndex) && !row.get(costIndex).isNull()) {
                        cost = new BigDecimal(row.get(costIndex).asText());
                    }

                    if (dateIndex != -1 && row.has(dateIndex) && !row.get(dateIndex).isNull()) {
                        String dateStr = row.get(dateIndex).asText();
                        if (dateStr.length() == 8 && !dateStr.contains("-")) {
                            usageDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
                        } else if (dateStr.contains("T")) {
                            usageDate = LocalDate.parse(dateStr.split("T")[0]);
                        } else {
                            usageDate = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                        }
                    }

                    if (currencyIndex != -1 && row.has(currencyIndex) && !row.get(currencyIndex).isNull()) {
                        currency = row.get(currencyIndex).asText();
                    }

                    if (usageDate != null) {
                        list.add(AzureDailyCost.builder()
                                .recordDate(usageDate)
                                .costAmount(cost)
                                .currency(currency)
                                .build());
                    }
                }
            }
            return list;

        } catch (Exception e) {
            log.error("Error fetching Azure daily costs from API: ", e);
            throw new InfrastructureException(
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
            throw new InfrastructureException(
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
