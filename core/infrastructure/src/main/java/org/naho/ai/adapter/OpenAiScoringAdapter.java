    package org.naho.ai.adapter;

    import org.naho.ai.config.OpenAiProperties;
    import org.naho.ai.port.out.AiScoringPort;
    import org.naho.ai.result.ScoringResult;
    import com.fasterxml.jackson.databind.JsonNode;
    import com.fasterxml.jackson.databind.ObjectMapper;

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

        private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

        private static final String SCORING_SYSTEM_PROMPT = """
                You are a strict Japanese speaking evaluator for conversational AI language learning. \
                Evaluate the learner fairly and consistently based on real communicative ability. \
                Focus on: fluency, pronunciation, grammar, vocabulary, interaction, naturalness, coherence. \
                Do not over-score weak communication.

                SCORING GUIDELINES
                0-20: Cannot communicate meaningfully.
                21-40: Very limited Japanese with major breakdowns.
                41-60: Basic communication possible but unnatural and error-prone.
                61-75: Functional conversation with noticeable mistakes.
                76-85: Strong conversational ability with mostly natural responses.
                86-93: Advanced fluent communication with high naturalness.
                94-100: Near-native conversational Japanese.

                EVALUATION RULES
                - Fluency: Evaluate speaking flow, pauses, hesitation, fillers, and response speed.
                - Pronunciation: Evaluate intelligibility, sound clarity, rhythm, and naturalness. Do not heavily penalize understandable foreign accents.
                - Grammar: Evaluate particles, conjugation, sentence structure, and grammatical accuracy.
                - Vocabulary: Evaluate variety, appropriateness, and expression quality.
                - Interaction: Evaluate relevance, turn-taking, conversational continuation, and engagement.
                - Naturalness: Evaluate whether responses sound like authentic modern Japanese.
                - Coherence: Evaluate logical flow and topic consistency.

                IMPORTANT CALIBRATION
                - Frequent grammar mistakes should not score above 75.
                - Excessive hesitation lowers fluency.
                - Robotic or textbook-only responses lower naturalness.
                - Very short responses lower interaction and vocabulary scores.
                - Scores above 90 require highly natural Japanese.

                SPEECH METADATA USAGE
                If provided, use: speech_rate_wpm, average_pause_ms, filler_count, pronunciation_score.
                High filler count and long pauses reduce fluency.

                JLPT ESTIMATION
                Estimate: N5 / N4 / N3 / N2 / N1

                FEEDBACK RULES
                Feedback must be: concise, specific, actionable. Avoid generic praise.

                OUTPUT: Return ONLY valid JSON — no markdown fences, no extra text.
                {
                "overall_score": <integer 0-100>,
                "jlpt_estimate": "N5|N4|N3|N2|N1",
                "scores": {
                    "fluency": <integer 0-100>,
                    "pronunciation": <integer 0-100>,
                    "grammar": <integer 0-100>,
                    "vocabulary": <integer 0-100>,
                    "interaction": <integer 0-100>,
                    "naturalness": <integer 0-100>,
                    "coherence": <integer 0-100>
                },
                "summary": "<string>",
                "strengths": ["<string>", ...],
                "weaknesses": ["<string>", ...],
                "feedback": {
                    "fluency": "<string>",
                    "grammar": "<string>",
                    "vocabulary": "<string>",
                    "interaction": "<string>",
                    "naturalness": "<string>"
                },
                "improved_expressions": [
                    { "original": "<string>", "improved": "<string>" }
                ]
                }
                """;

        private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

        private final OpenAiProperties properties;
        private final HttpClient httpClient;

        public OpenAiScoringAdapter(OpenAiProperties properties) {
            this.properties = properties;
            this.httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();
        }

        @Override
        public ScoringResult score(String sessionId,
                                String topic,
                                String fullTranscript,
                                String speechMetadata,
                                String asrConfidence) {
            System.out.println("[OpenAiScoringAdapter] Calling model: " + properties.scoringModel());

            String userContent = buildUserContent(topic, fullTranscript, speechMetadata, asrConfidence);
            String requestBody = buildScoringRequestBody(userContent);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL))
                    .timeout(Duration.ofMinutes(10))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + properties.apiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            try {
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200) {
                    throw new RuntimeException("Scoring API error. Status: " + response.statusCode() + " | " + response.body());
                }
                String rawContent = extractContent(response.body());
                System.out.println("[OpenAiScoringAdapter] Raw JSON: " + rawContent);
                return parseScoringResult(sessionId, rawContent);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Scoring request interrupted", e);
            } catch (Exception e) {
                throw new RuntimeException("Cannot call scoring API: " + e.getMessage(), e);
            }
        }


        private String buildUserContent(String topic,
                                        String conversation,
                                        String speechMetadata,
                                        String asrConfidence) {
            String safeTopic = (topic != null && !topic.isBlank()) ? topic : "General conversation";
            String safeMeta  = (speechMetadata != null && !speechMetadata.isBlank()) ? speechMetadata : "N/A";
            String safeAsr   = (asrConfidence  != null && !asrConfidence.isBlank())  ? asrConfidence  : "N/A";

            return "Topic: " + safeTopic + "\n\n"
                    + "Conversation:\n" + conversation + "\n\n"
                    + "Speech Metadata: " + safeMeta + "\n\n"
                    + "ASR Confidence: " + safeAsr;
        }

        private String buildScoringRequestBody(String userContent) {
            String escapedSystem  = escapeJson(SCORING_SYSTEM_PROMPT);
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
                    """.formatted(properties.scoringModel(), escapedSystem, escapedContent);
        }


        private String extractContent(String responseJson) {
            try {
                JsonNode root = OBJECT_MAPPER.readTree(responseJson);
                return root.path("choices").path(0).path("message").path("content").asText("");
            } catch (Exception e) {
                throw new RuntimeException("Cannot parse content field from response: " + responseJson, e);
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


        private ScoringResult parseScoringResult(String sessionId, String json) {
            String cleaned = extractJsonBlock(json);
            try {
                JsonNode root = OBJECT_MAPPER.readTree(cleaned);

                int overallScore     = root.path("overall_score").asInt(0);
                String jlptEstimate  = root.path("jlpt_estimate").asText("N5");

                JsonNode scoresNode = root.path("scores");
                int fluency      = scoresNode.path("fluency").asInt(0);
                int pronunciation= scoresNode.path("pronunciation").asInt(0);
                int grammar      = scoresNode.path("grammar").asInt(0);
                int vocabulary   = scoresNode.path("vocabulary").asInt(0);
                int interaction  = scoresNode.path("interaction").asInt(0);
                int naturalness  = scoresNode.path("naturalness").asInt(0);
                int coherence    = scoresNode.path("coherence").asInt(0);

                String summary = root.path("summary").asText("");

                List<String> strengths  = new ArrayList<>();
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
                        improvedExpressions.add(new ScoringResult.ImprovedExpression(original, improved));
                    }
                }

                return new ScoringResult(
                        sessionId,
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
                        improvedExpressions
                );
            } catch (Exception e) {
                throw new RuntimeException("Cannot parse scoring JSON: " + cleaned, e);
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
