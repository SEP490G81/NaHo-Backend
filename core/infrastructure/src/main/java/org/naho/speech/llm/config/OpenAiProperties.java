package org.naho.speech.llm.config;

public record OpenAiProperties(
        String apiKey,
        String chatModel,
        String scoringModel,
        int maxTokens,
        double temperature
) {
}
