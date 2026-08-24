package org.naho.speech.llm.conversation.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.constant.OpenAiConfigProperties;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

@Component
public class OpenAiChatHelper {
    private static final String LLM_URL = "https://api.openai.com/v1/chat/completions";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final OpenAiConfigProperties openAiConfigProperties;
    private final HttpClient httpClient;

    public OpenAiChatHelper(OpenAiConfigProperties openAiConfigProperties) {
        this.openAiConfigProperties = openAiConfigProperties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    public String executeRequest(String requestBody, Duration timeout) {
        HttpRequest request = buildHttpRequest(requestBody, timeout);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new InfrastructureException(
                        LlmApplicationError.LLM_API_ERROR,
                        LlmDetailMessageKey.LLM_API_ERROR,
                        "Status: " + response.statusCode() + " | " + response.body());
            }
            return extractContent(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    LlmApplicationError.LLM_CONNECTION_TIMEOUT,
                    LlmDetailMessageKey.LLM_CONNECTION_TIMEOUT,
                    e.getMessage());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_API_ERROR,
                    LlmDetailMessageKey.LLM_API_ERROR,
                    e.getMessage());
        }
    }

    public String buildContextRequestBody(String messagesJson) {
        return String.format(
                Locale.US,
                "{\"model\":\"%s\",\"messages\":%s,\"max_tokens\":%d,\"temperature\":%.1f}",
                openAiConfigProperties.getChatModel(),
                messagesJson,
                openAiConfigProperties.getMaxTokens(),
                openAiConfigProperties.getHighTemperature()
        );
    }

    public HttpRequest buildHttpRequest(String body, Duration timeout) {
        return HttpRequest.newBuilder()
                .uri(URI.create(LLM_URL))
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openAiConfigProperties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    public String extractContent(String responseJson) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseJson);
            return root.path("choices").path(0).path("message").path("content").asText("");
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage());
        }
    }
}
