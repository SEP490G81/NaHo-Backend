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
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAiScoringAdapter implements AiScoringPort {
    private static final String PROMPT_TEMPLATE_PATH = "/prompt_template/scoring_session.prompt";
    private static final String LLM_URL = "https://api.openai.com/v1/chat/completions";

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
    public SpeakingSessionAssessmentResult score(String sessionCode, String topic, String systemPromptContent, String messagesJson) {
        String userContent = buildUserContent(topic, systemPromptContent);
        String requestBody = buildScoringRequestBody()
    }

    @Override
    public SpeakingSessionAssessmentResult score(
            String sessionCode,
            String topic,
            String fullTranscript,
            String speechMetadata,
            String asrConfidence,
            String personaContext
    ) {
        System.out.println("[OpenAiScoringAdapter] Calling model: " + properties.getScoringModel());

        String requestBody = buildScoringRequestBody(userContent);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LLM_URL))
                .timeout(Duration.ofMinutes(10))
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
            String rawContent = extractContent(response.body());
            System.out.println("[OpenAiScoringAdapter] Raw JSON: " + rawContent);
            return parseScoringResult(rawContent);

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

    private String buildUserContent(
            String topic,
            String systemPromptContent
    ) {
        return """
                Persona Context:
                {{personaContext}}
                Topic: {{topic}}
                """
                .replace("{{personaContext}}", systemPromptContent)
                .replace("{{topic}}", topic);
    }

    private String buildScoringRequestBody(String messagesJson) {
        String systemPrompt = loadPromptTemplate();
        String escapedSystem = escapeJson(systemPrompt);
        String escapedContent = escapeJson(messagesJson);
        return """
                {
                "model": "%s",
                "messages": [
                    { "role": "system", "content": "%s" },
                    { "role": "user",   "content": "%s" }
                ],
                "max_completion_tokens": 2000
                }
                """.formatted(properties.getScoringModel(), escapedSystem, escapedContent);
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


    private String extractJsonBlock(String text) {
        if (text == null) {
            return "{}";
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        return text.trim();
    }


    private SpeakingSessionAssessmentResult parseScoringResult(String json) {
        String cleaned = extractJsonBlock(json);
        try {
            JsonNode root = OBJECT_MAPPER.readTree(cleaned);

            int overallScore = root.path("overall_score").asInt(0);
            String jlptEstimate = root.path("jlpt_estimate").asText("N5");

            JsonNode scoresNode = root.path("scores");
            int fluency = scoresNode.path("fluency").asInt(0);
            int pronunciation = scoresNode.path("pronunciation").asInt(0);
            int grammar = scoresNode.path("grammar").asInt(0);
            int vocabulary = scoresNode.path("vocabulary").asInt(0);
            int interaction = scoresNode.path("interaction").asInt(0);
            int naturalness = scoresNode.path("naturalness").asInt(0);
            int coherence = scoresNode.path("coherence").asInt(0);

            String summary = root.path("summary").asText("");

            List<String> strengths = new ArrayList<>();
            JsonNode strengthsNode = root.path("strengths");
            if (strengthsNode.isArray()) {
                for (JsonNode node : strengthsNode) {
                    strengths.add(node.asText());
                }
            }

            List<String> weaknesses = new ArrayList<>();
            JsonNode weaknessesNode = root.path("weaknesses");
            if (weaknessesNode.isArray()) {
                for (JsonNode node : weaknessesNode) {
                    weaknesses.add(node.asText());
                }
            }

            Map<String, String> feedback = new HashMap<>();
            JsonNode feedbackNode = root.path("feedback");
            if (feedbackNode.isObject()) {
                feedbackNode.fields().forEachRemaining(entry -> {
                    feedback.put(entry.getKey(), entry.getValue().asText());
                });
            }

            List<SpeakingImprovedExpressionResult> improvedExpressions = new ArrayList<>();
            JsonNode improvedNode = root.path("improved_expressions");
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
                            explanationVi
                    ));
                }
            }

            // Parse studyRecommendation
            String studyFocusArea = null;
            String studyRecommendation = null;
            String studyEncouragement = null;
            JsonNode studyNode = root.path("studyRecommendation");
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
                    .fluencyScore(fluency)
                    .pronunciationScore(pronunciation)
                    .grammarScore(grammar)
                    .vocabularyScore(vocabulary)
                    .interactionScore(interaction)
                    .naturalnessScore(naturalness)
                    .coherenceScore(coherence)
                    .summary(summary)
                    .strengths(strengthsJson)
                    .weaknesses(weaknessesJson)
                    .feedbackFluency(feedback.get("fluency"))
                    .feedbackPronunciation(feedback.get("pronunciation"))
                    .feedbackGrammar(feedback.get("grammar"))
                    .feedbackVocabulary(feedback.get("vocabulary"))
                    .feedbackInteraction(feedback.get("interaction"))
                    .feedbackNaturalness(feedback.get("naturalness"))
                    .feedbackCoherence(feedback.get("coherence"))
                    .studyFocusArea(studyFocusArea)
                    .studyRecommendation(studyRecommendation)
                    .studyEncouragement(studyEncouragement)
                    .speakingImprovedExpressions(improvedExpressions)
                    .build();
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
