package org.naho.speech.llm.conversation.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.constant.AiMessageField;
import org.naho.speech.llm.conversation.constant.OpenAiConfigProperties;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.port.out.AiChatPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OpenAiChatAdapter implements AiChatPort {

    private static final String LLM_URL = "https://api.openai.com/v1/chat/completions";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final OpenAiConfigProperties properties;
    private final HttpClient httpClient;

    public OpenAiChatAdapter(OpenAiConfigProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public String chatWithContext(List<Map<String, String>> messages) {
        String requestBody = buildContextRequestBody(messages);
        return executeRequest(requestBody, Duration.ofSeconds(60));
    }

    @Override
    public String buildMessagesRequestBody(List<Map<String, String>> messages) {
        try {
            StringBuilder messagesJson = new StringBuilder("[");

            for (Map<String, String> map : messages) {
                messagesJson.append(String.format(
                        "{\"role\":\"%s\",\"content\":%s}",
                        map.get(AiMessageField.ROLE),
                        OBJECT_MAPPER.writeValueAsString(map.get(AiMessageField.CONTENT))
                ));
                messagesJson.append(",");
            }

            // xóa dấu phẩy thừa ở cuối
            if (messagesJson.length() > 1) {
                messagesJson.deleteCharAt(messagesJson.length() - 1);
            }

            messagesJson.append("]");

            return messagesJson.toString();
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage());
        }
    }

    private String executeRequest(String requestBody, Duration timeout) {
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

    private String buildContextRequestBody(List<Map<String, String>> messages) {
        try {
            String messagesJson = buildMessagesRequestBody(messages);

            return String.format(
                    Locale.US,
                    "{\"model\":\"%s\",\"messages\":%s,\"max_tokens\":%d,\"temperature\":%.1f}",
                    properties.getChatModel(),
                    messagesJson,
                    properties.getMaxTokens(),
                    properties.getTemperature()
            );
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage());
        }
    }

    private HttpRequest buildHttpRequest(String body, Duration timeout) {
        return HttpRequest.newBuilder()
                .uri(URI.create(LLM_URL))
                .timeout(timeout)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + properties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private String extractContent(String responseJson) {
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
