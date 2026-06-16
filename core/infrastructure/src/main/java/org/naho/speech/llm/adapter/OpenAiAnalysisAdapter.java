package org.naho.speech.llm.adapter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.naho.shared.exception.InfrastructureException;
import org.naho.speech.llm.constant.OpenAiConfigProperties;
import org.naho.speech.llm.exception.LlmApplicationError;
import org.naho.speech.llm.port.out.AiAnalysisPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class OpenAiAnalysisAdapter implements AiAnalysisPort {

    private static final String LLM_URL = "https://api.openai.com/v1/chat/completions";
    private final OpenAiConfigProperties properties;
    private final HttpClient httpClient;

    public OpenAiAnalysisAdapter(OpenAiConfigProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public String analyzeSpeaking(String topic, String question, String studentTranscript, String azureWordFeedbackJson) {
        String systemPrompt = """
            You are an expert Japanese language assessor.
            Evaluate the student's spoken Japanese answer based on the context.
            Focus on grammatical accuracy, vocabulary usage, and naturalness.
            
            Based on the student's transcript and the word-level pronunciation scores/errors from Azure Speech, perform a detailed evaluation.
            
            Return the results ONLY as a valid JSON object matching the following schema.
            Do NOT include any markdown formatting (like ```json or ```), no leading/trailing comments, and no extra text. It must be a raw parseable JSON string.
            
            JSON Schema:
            {
              "scores": {
                "vocabulary": <double 0.0-10.0>,
                "grammar": <double 0.0-10.0>,
                "naturalness": <double 0.0-10.0>
              },
              "userTranscript": [
                {
                  "text": "<segment of user's answer>",
                  "error": null
                },
                {
                  "text": "<segment containing error>",
                  "error": {
                    "type": "Ngữ pháp / Sự tự nhiên / Từ vựng",
                    "explanation": "<Vietnamese explanation of the error>",
                    "suggestion": "<corrected Japanese version>"
                  }
                }
              ],
              "aiSuggestion": {
                "jp": "<natural Japanese recommended response>",
                "furigana": "<the recommended response with furigana/hiragana for all kanji>",
                "vi": "<Vietnamese translation of the recommended response>"
              },
              "pronunciationNote": "<Overall pronunciation advice in Vietnamese based on the azure word feedback. Focus on what areas the student needs to improve, e.g., long vowels, double consonants, or typical errors.>",
              "wordNotes": {
                "<japanese_word>": "<Vietnamese feedback note for this specific word, e.g., 'Phát âm tốt', 'Chú ý kéo dài hơi', etc. Keep it very short and helpful.>"
              },
              "expressions": [
                {
                  "jp": "<useful Japanese phrase related to this topic>",
                  "furigana": "<furigana for the phrase>",
                  "vi": "<Vietnamese translation>",
                  "note": "<Vietnamese note on how/when to use it>"
                }
              ],
              "itVocab": [
                {
                  "term": "<IT Japanese vocabulary, e.g., 進捗>",
                  "reading": "<reading in hiragana>",
                  "meaning": "<Vietnamese meaning>"
                }
              ]
            }
            
            NOTE for itVocab: ONLY populate itVocab with 1-3 useful IT Japanese terms if the topic is IT/tech related. Otherwise, leave it as an empty list [].
            """;

        String userContent = String.format(
                "Topic: %s\nQuestion: %s\nStudent Transcript: %s\nAzure Pronunciation Data: %s",
                topic, question, studentTranscript, azureWordFeedbackJson
        );

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
                        "OpenAI Analysis API error. Status: " + response.statusCode() + " | " + response.body());
            }
            return extractContent(response.body());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InfrastructureException(
                    LlmApplicationError.LLM_CONNECTION_TIMEOUT,
                    "Analysis request interrupted", e
            );
        } catch (Exception e) {
            throw new InfrastructureException(
                    LlmApplicationError.LLM_API_ERROR,
                    "Cannot call OpenAI Analysis API: " + e.getMessage(), e
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
                    "Cannot parse OpenAI response content: " + responseJson, e
            );
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "\"\"";
        return new ObjectMapper().valueToTree(input).toString();
    }
}
