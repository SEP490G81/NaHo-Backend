package org.naho.config;

import org.naho.ai.adapter.AzureSpeechToTextAdapter;
import org.naho.ai.adapter.InMemorySessionStore;
import org.naho.ai.adapter.OpenAiChatAdapter;
import org.naho.ai.adapter.OpenAiScoringAdapter;
import org.naho.ai.config.OpenAiProperties;
import org.naho.ai.port.in.EndSessionInputPort;
import org.naho.ai.port.in.SpeakingSessionInputPort;
import org.naho.ai.port.in.SuggestedTopicsInputPort;
import org.naho.ai.port.out.AiChatPort;
import org.naho.ai.port.out.AiScoringPort;
import org.naho.ai.port.out.SessionStorePort;
import org.naho.ai.port.out.SpeechToTextPort;
import org.naho.ai.usecase.EndSessionUseCase;
import org.naho.ai.usecase.SpeakingSessionUseCase;
import org.naho.ai.usecase.SuggestedTopicsUseCase;
import org.naho.speech.azure.port.out.AzureSpeechService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bootstrap Configuration: Liên kết các UseCase, Port, Adapter cho module AI Speaking.
 *
 * Luồng wiring:
 *   OpenAiConfigProperties → OpenAiProperties (record)
 *       → OpenAiChatAdapter (AiChatPort)
 *       → OpenAiScoringAdapter (AiScoringPort)
 *
 *   InMemorySessionStore (SessionStorePort)
 *   AzureSpeechToTextAdapter (SpeechToTextPort) ← bridge với AzureSpeechService
 *
 *   SpeakingSessionUseCase (SpeakingSessionInputPort)
 *   EndSessionUseCase (EndSessionInputPort)
 *   SuggestedTopicsUseCase (SuggestedTopicsInputPort)
 */
@Configuration
public class ChatConfig {

    // ─── External Config ─────────────────────────────────────────

    @Bean
    @ConfigurationProperties(prefix = "openai")
    public OpenAiConfigProperties openAiConfigProperties() {
        return new OpenAiConfigProperties();
    }

    @Bean
    public OpenAiProperties openAiProperties(OpenAiConfigProperties config) {
        return new OpenAiProperties(
                config.getApiKey(),
                config.getChatModel(),
                config.getScoringModel(),
                config.getMaxTokens(),
                config.getTemperature()
        );
    }

    // ─── Output Port Adapters ────────────────────────────────────

    @Bean
    public AiChatPort aiChatPort(OpenAiProperties openAiProperties) {
        return new OpenAiChatAdapter(openAiProperties);
    }

    @Bean
    public SessionStorePort sessionStorePort() {
        return new InMemorySessionStore();
    }

    @Bean
    public AiScoringPort aiScoringPort(OpenAiProperties openAiProperties) {
        return new OpenAiScoringAdapter(openAiProperties);
    }

    @Bean
    public SpeechToTextPort speechToTextPort(AzureSpeechService azureSpeechService) {
        return new AzureSpeechToTextAdapter(azureSpeechService);
    }

    // ─── Input Port UseCases ─────────────────────────────────────

    @Bean
    public SpeakingSessionInputPort speakingSessionInputPort(
            AiChatPort aiChatPort,
            SessionStorePort sessionStorePort,
            SpeechToTextPort speechToTextPort
    ) {
        return new SpeakingSessionUseCase(aiChatPort, sessionStorePort, speechToTextPort);
    }

    @Bean
    public EndSessionInputPort endSessionInputPort(
            SessionStorePort sessionStorePort,
            AiScoringPort aiScoringPort
    ) {
        return new EndSessionUseCase(sessionStorePort, aiScoringPort);
    }

    @Bean
    public SuggestedTopicsInputPort suggestedTopicsInputPort() {
        return new SuggestedTopicsUseCase();
    }
}
