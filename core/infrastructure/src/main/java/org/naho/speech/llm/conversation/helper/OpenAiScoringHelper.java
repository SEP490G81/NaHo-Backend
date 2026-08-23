package org.naho.speech.llm.conversation.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.naho.i18n.message.llm.LlmDetailMessageKey;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.conversation.constant.AiMessageField;
import org.naho.speech.llm.conversation.constant.OpenAiConfigProperties;
import org.naho.speech.llm.conversation.exception.LlmApplicationError;
import org.naho.speech.llm.model.conversation.SpeakingImprovedExpression;
import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;
import org.naho.speech.llm.type.SenderType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAiScoringHelper {
    private static final String PROMPT_TEMPLATE_PATH = "/prompt_template/scoring_session.prompt";
    private static final String LLM_URL = "https://api.openai.com/v1/chat/completions";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final OpenAiConfigProperties openAiConfigProperties;

    public String buildScoringContext(
            String topic,
            String systemPromptContent,
            String messagesJson
    ) {
        return """
                ## Persona Context:
                {{personaContext}}
                ## Topic:
                {{topic}}
                ## Messages History:
                {{messagesHistory}}
                """
                .replace("{{personaContext}}", systemPromptContent != null ? systemPromptContent : "")
                .replace("{{topic}}", topic != null ? topic : "")
                .replace("{{messagesHistory}}", messagesJson != null ? messagesJson : "");
    }

    public String buildScoringRequestBody(String userContent) {
        String systemPrompt = loadPromptTemplate();
        String scoringModel = openAiConfigProperties.getScoringModel();

        Map<String, Object> userMessage = Map.of(
                AiMessageField.ROLE, SenderType.USER.name().toLowerCase(),
                AiMessageField.CONTENT, userContent
        );

        Map<String, Object> systemMessage = Map.of(
                AiMessageField.ROLE, SenderType.SYSTEM.name().toLowerCase(),
                AiMessageField.CONTENT, systemPrompt
        );

        Map<String, Object> requestPayload = Map.of(
                "model", scoringModel,
                "messages", List.of(userMessage, systemMessage),
                "temperature", openAiConfigProperties.getLowTemperature(),
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

    public HttpRequest buildHttpRequest(String requestBody) {
        return HttpRequest.newBuilder()
                .uri(URI.create(LLM_URL))
                .timeout(openAiConfigProperties.getRequestTimeout())
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openAiConfigProperties.getApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }

    public String extractContent(String responseJson) {
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

    public String extractJsonBlock(String text) {
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

    public SpeakingSessionAssessment parseScoringResult(String rawJsonContent) {
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

            String studyFocusArea = null;
            String studyRecommendation = null;
            String studyEncouragement = null;
            JsonNode studyNode = rootNode.path("studyRecommendation");
            if (!studyNode.isMissingNode() && studyNode.isObject()) {
                studyFocusArea = studyNode.path("focusArea").asText("");
                studyRecommendation = studyNode.path("suggestedPractice").asText("");
                studyEncouragement = studyNode.path("encouragement").asText("");
            }

            String strengthsJson = OBJECT_MAPPER.writeValueAsString(strengths);
            String weaknessesJson = OBJECT_MAPPER.writeValueAsString(weaknesses);

            List<SpeakingImprovedExpression> speakingImprovedExpressions = new ArrayList<>();
            JsonNode improvedNode = rootNode.path("improved_expressions");
            if (improvedNode.isArray()) {
                for (int i = 0; i < improvedNode.size(); i++) {
                    JsonNode node = improvedNode.get(i);
                    String original = node.path("original").asText("");
                    String improved = node.path("improved").asText("");
                    String explanationVi = node.path("explanationVi").asText("");
                    speakingImprovedExpressions.add(
                            SpeakingImprovedExpression.builder()
                                    .originalText(original)
                                    .improvedText(improved)
                                    .explanationVietnamese(explanationVi)
                                    .build()
                    );
                }
            }

            return SpeakingSessionAssessment.builder()
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
                    .speakingImprovedExpressions(speakingImprovedExpressions)
                    .build();

        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_PARSE_ERROR,
                    LlmDetailMessageKey.LLM_PARSE_ERROR,
                    e.getMessage());
        }
    }

    public String loadPromptTemplate() {
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
