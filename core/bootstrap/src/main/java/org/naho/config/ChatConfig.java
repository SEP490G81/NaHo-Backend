package org.naho.config;

import org.naho.speech.llm.adapter.AzureSpeechToTextAdapter;
import org.naho.speech.llm.adapter.InMemorySessionStore;
import org.naho.speech.llm.adapter.OpenAiChatAdapter;
import org.naho.speech.llm.adapter.OpenAiScoringAdapter;
import org.naho.speech.llm.config.OpenAiProperties;
import org.naho.speech.llm.port.in.EndSessionInputPort;
import org.naho.speech.llm.port.in.SpeakingSessionInputPort;
import org.naho.speech.llm.port.in.SuggestedTopicsInputPort;
import org.naho.speech.llm.port.out.AiChatPort;
import org.naho.speech.llm.port.out.AiScoringPort;
import org.naho.speech.llm.port.out.SessionStorePort;
import org.naho.speech.llm.port.out.SpeechToTextPort;
import org.naho.speech.llm.usecase.EndSessionUseCase;
import org.naho.speech.llm.usecase.SpeakingSessionUseCase;
import org.naho.speech.llm.usecase.SuggestedTopicsUseCase;
import org.naho.speech.azure.port.out.AzureSpeechServicePort;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bootstrap Configuration: Liên kết các UseCase, Port, Adapter cho module AI Speaking.
 * <p>
 * Luồng wiring:
 * OpenAiConfigProperties → OpenAiProperties (record)
 * → OpenAiChatAdapter (AiChatPort)
 * → OpenAiScoringAdapter (AiScoringPort)
 * <p>
 * InMemorySessionStore (SessionStorePort)
 * AzureSpeechToTextAdapter (SpeechToTextPort) ← bridge với AzureSpeechService
 * <p>
 * SpeakingSessionUseCase (SpeakingSessionInputPort)
 * EndSessionUseCase (EndSessionInputPort)
 * SuggestedTopicsUseCase (SuggestedTopicsInputPort)
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
    public SpeechToTextPort speechToTextPort(AzureSpeechServicePort azureSpeechServicePort) {
        return new AzureSpeechToTextAdapter(azureSpeechServicePort);
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
