package org.naho.speech.llm.question.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.constant.OpenAiConfigProperties;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.question.command.QuestionContextCommand;
import org.naho.speech.llm.question.port.out.AiQuestionAnalysisPort;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AiQuestionAnalysisAdapter implements AiQuestionAnalysisPort {
    private static final String PROMPT_TEMPLATE_PATH = "/prompt_template/speaking_question_evaluation.prompt";
    private static final String LLM_URL = "https://api.openai.com/v1/chat/completions";
    private final OpenAiConfigProperties properties;
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    @Override
    public String analyzeSpeaking(QuestionContextCommand command) {
        String systemPromptTemplate = loadPromptTemplate();

        String systemPrompt = String.format(
                systemPromptTemplate,
                command.curriculum(),
                command.level(),
                command.lessonOrder(),
                command.topic(),
                command.lesson(),
                command.canDoObjective(),
                command.grammarFocus(),
                command.vocabularyFocus(),
                command.questionTitle(),
                command.questionDescription(),
                command.learnerTranscript()
        );

        String userContent = "Please evaluate the learner's transcript based on the system prompt instruction.";
        String requestBody = buildRequestBody(systemPrompt, userContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LLM_URL))
                .timeout(Duration.ofSeconds(60))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + properties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

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
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_API_ERROR,
                    LlmDetailMessageKey.LLM_API_ERROR,
                    e.getMessage()
            );
        }
    }

    private String buildRequestBody(String systemPrompt, String userContent) {
        String model = properties.getScoringModel();
        if (model == null || model.isBlank()) {
            model = "gpt-4o";
        }
        return String.format(
                "{\"model\":\"%s\",\"messages\":[{\"role\":\"system\",\"content\":%s},{\"role\":\"user\",\"content\":%s}],\"temperature\":0.2}",
                model,
                escapeJson(systemPrompt),
                escapeJson(userContent)
        );
    }

    private String extractContent(String responseJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseJson);

            String rawContent = root.path("choices").path(0).path("message").path("content").asText("");
            String trimmed = rawContent.trim();
            if (trimmed.startsWith("```")) {
                int firstLineBreak = trimmed.indexOf('\n');
                int lastFence = trimmed.lastIndexOf("```");
                if (firstLineBreak != -1 && lastFence != -1 && lastFence > firstLineBreak) {
                    trimmed = trimmed.substring(firstLineBreak + 1, lastFence).trim();
                }
            }
            return trimmed;
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage()
            );
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "\"\"";
        return new ObjectMapper().valueToTree(input).toString();
    }

    private String loadPromptTemplate() {
        try (var is = getClass().getResourceAsStream(PROMPT_TEMPLATE_PATH)) {
            if (is == null) {
                throw new IllegalStateException("Prompt template not found: " + PROMPT_TEMPLATE_PATH);
            }
            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prompt template: " + PROMPT_TEMPLATE_PATH, e);
        }
    }
}
