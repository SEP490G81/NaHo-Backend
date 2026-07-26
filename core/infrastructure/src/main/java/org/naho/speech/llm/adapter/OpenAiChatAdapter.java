package org.naho.speech.llm.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.constant.OpenAiConfigProperties;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.llm.port.out.AiChatPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
    public String chat(String userMessage) {
        String requestBody = buildSimpleRequestBody(userMessage, false);
        return executeRequest(requestBody, Duration.ofSeconds(60));
    }

    @Override
    public void chatStream(String userMessage, Consumer<String> onToken) {
        String requestBody = buildSimpleRequestBody(userMessage, true);
        executeStreamRequest(requestBody, onToken);
    }

    @Override
    public String chatWithContext(List<Map<String, String>> messages) {
        String requestBody = buildContextRequestBody(messages, false);
        return executeRequest(requestBody, Duration.ofSeconds(60));
    }

    @Override
    public void chatStreamWithContext(List<Map<String, String>> messages, Consumer<String> onToken) {
        String requestBody = buildContextRequestBody(messages, true);
        executeStreamRequest(requestBody, onToken);
    }

    private String executeRequest(String requestBody, Duration timeout) {
        HttpRequest request = buildHttpRequest(requestBody, timeout);

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new InfrastructureException(
                        LlmApplicationError.LLM_API_ERROR,
                        LlmDetailMessageKey.LLM_API_ERROR,
                        "Status: " + response.statusCode() + " | " + response.body()
                );
            }
            return extractContent(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    LlmApplicationError.LLM_CONNECTION_TIMEOUT,
                    LlmDetailMessageKey.LLM_CONNECTION_TIMEOUT,
                    e.getMessage()
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_API_ERROR,
                    LlmDetailMessageKey.LLM_API_ERROR,
                    e.getMessage()
            );
        }
    }

    private void executeStreamRequest(String requestBody, Consumer<String> onToken) {
        HttpRequest request = buildHttpRequest(requestBody, Duration.ofSeconds(120));

        try {
            HttpResponse<Stream<String>> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofLines());

            if (response.statusCode() != 200) {
                String errorBody = response.body().reduce("", (a, b) -> a + b);
                throw new InfrastructureException(
                        LlmApplicationError.LLM_STREAMING_ERROR,
                        LlmDetailMessageKey.LLM_STREAMING_ERROR,
                        "Status: " + response.statusCode() + " | " + errorBody
                );
            }

            response.body().forEach(line -> {
                if (!line.startsWith("data: ")) return;

                String payload = line.substring(6).trim();
                if ("[DONE]".equals(payload)) return;

                String token = extractDeltaContent(payload);
                if (token != null && !token.isEmpty()) {
                    onToken.accept(token);
                }
            });

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    LlmApplicationError.LLM_CONNECTION_TIMEOUT,
                    LlmDetailMessageKey.LLM_CONNECTION_TIMEOUT,
                    e.getMessage()
            );
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_STREAMING_ERROR,
                    LlmDetailMessageKey.LLM_STREAMING_ERROR,
                    e.getMessage()
            );
        }
    }

    private String buildContextRequestBody(List<Map<String, String>> messages, boolean stream) {
        try {
            StringBuilder messagesJson = new StringBuilder("[");
            for (int i = 0; i < messages.size(); i++) {
                Map<String, String> msg = messages.get(i);
                if (i > 0) messagesJson.append(",");
                messagesJson.append(String.format(
                        "{\"role\":\"%s\",\"content\":%s}",
                        msg.get("role"),
                        OBJECT_MAPPER.writeValueAsString(msg.get("content"))
                ));
            }
            messagesJson.append("]");

            String streamField = stream ? ",\"stream\":true" : "";

            return String.format(
                    "{\"model\":\"%s\",\"messages\":%s,\"max_tokens\":%d,\"temperature\":%.1f%s}",
                    properties.getChatModel(),
                    messagesJson,
                    properties.getMaxTokens(),
                    properties.getTemperature(),
                    streamField
            );
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage()
            );
        }
    }

    private String buildSimpleRequestBody(String userMessage, boolean stream) {
        String escapedMessage = escapeJson(userMessage);
        String streamField = stream ? ",\n  \"stream\": true" : "";
        return """
                {
                  "model": "%s",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ],
                  "max_tokens": %d,
                  "temperature": %.1f%s
                }
                """.formatted(
                properties.getChatModel(),
                escapedMessage,
                properties.getMaxTokens(),
                properties.getTemperature(),
                streamField
        );
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

    private String extractDeltaContent(String json) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(json);
            JsonNode contentNode = root.path("choices").path(0).path("delta").path("content");
            return contentNode.isMissingNode() ? null : contentNode.asText();
        } catch (Exception e) {
            return null;
        }
    }

    private String extractContent(String responseJson) {
        try {
            JsonNode root = OBJECT_MAPPER.readTree(responseJson);
            return root.path("choices").path(0).path("message").path("content").asText("");
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage()
            );
        }
    }

    private String escapeJson(String input) {
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
