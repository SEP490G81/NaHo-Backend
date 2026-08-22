package org.naho.speech.llm.conversation.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.constant.OpenAiConfigProperties;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.conversation.port.out.AiScoringPort;
import org.naho.speech.llm.conversation.result.SpeakingImprovedExpressionResult;
import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAiScoringAdapter implements AiScoringPort {

    private static final String PROMPT_TEMPLATE_PATH = "/prompt_template/scoring_session.prompt";
    private static final String LLM_URL = "https://api.openai.com/v1/chat/completions";
    private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(90);
    private static final double DEFAULT_TEMPERATURE = 0.2;
    private static final String DEFAULT_SCORING_MODEL = "gpt-4o";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final OpenAiConfigProperties properties;
    private final HttpClient httpClient;

    public OpenAiScoringAdapter(OpenAiConfigProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public SpeakingSessionAssessmentResult score(String sessionCode, String topic, String systemPromptContent,
            String messagesJson) {
        String userContent = buildUserContent(topic, systemPromptContent, messagesJson);
        String requestBody = buildScoringRequestBody(userContent);
        HttpRequest httpRequest = buildHttpRequest(requestBody);

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                throw new InfrastructureException(
                        LlmApplicationError.LLM_API_ERROR,
                        LlmDetailMessageKey.LLM_API_ERROR);
            }
            String rawContent = extractContent(httpResponse.body());
            return parseScoringResult(rawContent);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    LlmApplicationError.LLM_CONNECTION_TIMEOUT,
                    LlmDetailMessageKey.LLM_CONNECTION_TIMEOUT,
                    e.getMessage());
        } catch (InfrastructureException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_API_ERROR,
                    LlmDetailMessageKey.LLM_API_ERROR,
                    e.getMessage());
        }
    }

    private String buildUserContent(
            String topic,
            String systemPromptContent,
            String messagesJson) {
        return """
                Persona Context:
                {{personaContext}}
                Topic: {{topic}}
                Transcript:
                {{transcript}}
                """
                .replace("{{personaContext}}", systemPromptContent != null ? systemPromptContent : "")
                .replace("{{topic}}", topic != null ? topic : "")
                .replace("{{transcript}}", messagesJson != null ? messagesJson : "");
    }

    private String buildScoringRequestBody(String userContent) {
        String systemPrompt = loadPromptTemplate();
        String scoringModel = properties.getScoringModel();
        if (scoringModel == null || scoringModel.isBlank()) {
            scoringModel = DEFAULT_SCORING_MODEL;
        }

        Map<String, Object> systemMessage = Map.of(
                "role", "system",
                "content", systemPrompt);
        Map<String, Object> userMessage = Map.of(
                "role", "user",
                "content", userContent);

        Map<String, Object> requestPayload = Map.of(
                "model", scoringModel,
                "messages", List.of(systemMessage, userMessage),
                "temperature", DEFAULT_TEMPERATURE,
                "response_format", Map.of("type", "json_object"));

        try {
            return OBJECT_MAPPER.writeValueAsString(requestPayload);
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage()
            );
        }
    }

    private HttpRequest buildHttpRequest(String requestBody) {
        return HttpRequest.newBuilder()
                .uri(URI.create(LLM_URL))
                .timeout(REQUEST_TIMEOUT)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + properties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    private String extractContent(String responseJson) {
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(responseJson);
            return rootNode.path("choices").path(0).path("message").path("content").asText("");
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage());
        }
    }

    private String extractJsonBlock(String text) {
        if (text == null) {
            return "{}";
        }
        String trimmedText = text.trim();
        if (trimmedText.startsWith("```")) {
            int firstLineBreak = trimmedText.indexOf('\n');
            int lastFence = trimmedText.lastIndexOf("```");
            if (firstLineBreak != -1 && lastFence != -1 && lastFence > firstLineBreak) {
                trimmedText = trimmedText.substring(firstLineBreak + 1, lastFence).trim();
            }
        }
        int startIndex = trimmedText.indexOf('{');
        int endIndex = trimmedText.lastIndexOf('}');
        if (startIndex != -1 && endIndex != -1 && endIndex >= startIndex) {
            return trimmedText.substring(startIndex, endIndex + 1);
        }
        return trimmedText;
    }

    private SpeakingSessionAssessmentResult parseScoringResult(String rawJsonContent) {
        String cleanJson = extractJsonBlock(rawJsonContent);
        try {
            JsonNode rootNode = OBJECT_MAPPER.readTree(cleanJson);

            int overallScore = rootNode.path("overall_score").asInt(0);
            String jlptEstimate = rootNode.path("jlpt_estimate").asText("N5");

            JsonNode scoresNode = rootNode.path("scores");
            int fluencyScore = scoresNode.path("fluency").asInt(0);
            int pronunciationScore = scoresNode.path("pronunciation").asInt(0);
            int grammarScore = scoresNode.path("grammar").asInt(0);
            int vocabularyScore = scoresNode.path("vocabulary").asInt(0);
            int interactionScore = scoresNode.path("interaction").asInt(0);
            int naturalnessScore = scoresNode.path("naturalness").asInt(0);
            int coherenceScore = scoresNode.path("coherence").asInt(0);

            String summary = rootNode.path("summary").asText("");

            List<String> strengths = new ArrayList<>();
            JsonNode strengthsNode = rootNode.path("strengths");
            if (strengthsNode.isArray()) {
                for (JsonNode node : strengthsNode) {
                    strengths.add(node.asText());
                }
            }

            List<String> weaknesses = new ArrayList<>();
            JsonNode weaknessesNode = rootNode.path("weaknesses");
            if (weaknessesNode.isArray()) {
                for (JsonNode node : weaknessesNode) {
                    weaknesses.add(node.asText());
                }
            }

            Map<String, String> feedbackMap = new HashMap<>();
            JsonNode feedbackNode = rootNode.path("feedback");
            if (feedbackNode.isObject()) {
                feedbackNode.fields()
                        .forEachRemaining(entry -> feedbackMap.put(entry.getKey(), entry.getValue().asText()));
            }

            List<SpeakingImprovedExpressionResult> improvedExpressions = new ArrayList<>();
            JsonNode improvedNode = rootNode.path("improved_expressions");
            if (improvedNode.isArray()) {
                for (int i = 0; i < improvedNode.size(); i++) {
                    JsonNode node = improvedNode.get(i);
                    String original = node.path("original").asText("");
                    String improved = node.path("improved").asText("");
                    String explanationVi = node.path("explanationVi").asText(null);
                    improvedExpressions.add(new SpeakingImprovedExpressionResult(
                            null,
                            null,
                            i,
                            original,
                            improved,
                            explanationVi));
                }
            }

            String studyFocusArea = null;
            String studyRecommendation = null;
            String studyEncouragement = null;
            JsonNode studyNode = rootNode.path("studyRecommendation");
            if (!studyNode.isMissingNode() && studyNode.isObject()) {
                studyFocusArea = studyNode.path("focusArea").asText(null);
                studyRecommendation = studyNode.path("suggestedPractice").asText(null);
                studyEncouragement = studyNode.path("encouragement").asText(null);
            }

            String strengthsJson = OBJECT_MAPPER.writeValueAsString(strengths);
            String weaknessesJson = OBJECT_MAPPER.writeValueAsString(weaknesses);

            return SpeakingSessionAssessmentResult.builder()
                    .overallScore(overallScore)
                    .jlptEstimate(jlptEstimate)
                    .fluencyScore(fluencyScore)
                    .pronunciationScore(pronunciationScore)
                    .grammarScore(grammarScore)
                    .vocabularyScore(vocabularyScore)
                    .interactionScore(interactionScore)
                    .naturalnessScore(naturalnessScore)
                    .coherenceScore(coherenceScore)
                    .summary(summary)
                    .strengths(strengthsJson)
                    .weaknesses(weaknessesJson)
                    .feedbackFluency(feedbackMap.get("fluency"))
                    .feedbackPronunciation(feedbackMap.get("pronunciation"))
                    .feedbackGrammar(feedbackMap.get("grammar"))
                    .feedbackVocabulary(feedbackMap.get("vocabulary"))
                    .feedbackInteraction(feedbackMap.get("interaction"))
                    .feedbackNaturalness(feedbackMap.get("naturalness"))
                    .feedbackCoherence(feedbackMap.get("coherence"))
                    .studyFocusArea(studyFocusArea)
                    .studyRecommendation(studyRecommendation)
                    .studyEncouragement(studyEncouragement)
                    .speakingImprovedExpressions(improvedExpressions)
                    .build();
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage());
        }
    }

    private String loadPromptTemplate() {
        try (var inputStream = getClass().getResourceAsStream(PROMPT_TEMPLATE_PATH)) {
            if (inputStream == null) {
                throw new IllegalStateException("Prompt template not found: " + PROMPT_TEMPLATE_PATH);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prompt template: " + PROMPT_TEMPLATE_PATH, e);
        }
    }
}
