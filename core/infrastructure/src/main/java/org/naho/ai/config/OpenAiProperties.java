package org.naho.ai.config;

public record OpenAiProperties(
        String apiKey,
        String chatModel,
        String scoringModel,
        int maxTokens,
        double temperature
) {}
