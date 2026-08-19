package org.naho.speech.llm.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.constant.OpenAiConfigProperties;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.llm.port.out.AiScoringPort;
import org.naho.speech.llm.result.ScoringResult;

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
    public ScoringResult score(String sessionCode,
                               String topic,
                               String fullTranscript,
                               String speechMetadata,
                               String asrConfidence,
                               String personaContext) {
        System.out.println("[OpenAiScoringAdapter] Calling model: " + properties.getScoringModel());

        String userContent = buildUserContent(topic, fullTranscript, speechMetadata, asrConfidence, personaContext);
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
            return parseScoringResult(sessionCode, rawContent);
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


    private String buildUserContent(String topic,
                                    String conversation,
                                    String speechMetadata,
                                    String asrConfidence,
                                    String personaContext) {
        String safeTopic = (topic != null && !topic.isBlank()) ? topic : "General conversation";
        String safeMeta = (speechMetadata != null && !speechMetadata.isBlank()) ? speechMetadata : "N/A";
        String safeAsr = (asrConfidence != null && !asrConfidence.isBlank()) ? asrConfidence : "N/A";
        String safePersona = (personaContext != null && !personaContext.isBlank()) ? personaContext : "(No persona context — general evaluation)";

        return "Persona Context:\n" + safePersona + "\n"
                + "Topic: " + safeTopic + "\n\n"
                + "Conversation:\n" + conversation + "\n\n"
                + "Speech Metadata: " + safeMeta + "\n\n"
                + "ASR Confidence: " + safeAsr;
    }

    private String buildScoringRequestBody(String userContent) {
        String systemPrompt = loadPromptTemplate("/prompt_template/scoring_session.prompt");
        String escapedSystem = escapeJson(systemPrompt);
        String escapedContent = escapeJson(userContent);
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


    private ScoringResult parseScoringResult(String sessionCode, String json) {
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

            List<ScoringResult.ImprovedExpression> improvedExpressions = new ArrayList<>();
            JsonNode improvedNode = root.path("improved_expressions");
            if (improvedNode.isArray()) {
                for (JsonNode node : improvedNode) {
                    String original = node.path("original").asText("");
                    String improved = node.path("improved").asText("");
                    String explanationVi = node.path("explanationVi").asText(null);
                    improvedExpressions.add(new ScoringResult.ImprovedExpression(original, improved, explanationVi));
                }
            }

            // Parse studyRecommendation (new field)
            ScoringResult.StudyRecommendation studyRecommendation = null;
            JsonNode studyNode = root.path("studyRecommendation");
            if (!studyNode.isMissingNode() && studyNode.isObject()) {
                studyRecommendation = new ScoringResult.StudyRecommendation(
                        studyNode.path("focusArea").asText(null),
                        studyNode.path("reason").asText(null),
                        studyNode.path("suggestedPractice").asText(null),
                        studyNode.path("encouragement").asText(null)
                );
            }

            return new ScoringResult(
                    sessionCode,
                    overallScore,
                    jlptEstimate,
                    fluency,
                    pronunciation,
                    grammar,
                    vocabulary,
                    interaction,
                    naturalness,
                    coherence,
                    summary,
                    strengths,
                    weaknesses,
                    feedback,
                    improvedExpressions,
                    studyRecommendation
            );
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

    private String loadPromptTemplate(String path) {
        try (var is = getClass().getResourceAsStream(path)) {
            if (is == null) {
                throw new IllegalStateException("Prompt template not found: " + path);
            }
            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load prompt template: " + path, e);
        }
    }
}
