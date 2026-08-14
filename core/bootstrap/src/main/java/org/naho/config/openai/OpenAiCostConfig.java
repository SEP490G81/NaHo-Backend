package org.naho.config.openai;

import org.naho.speech.llm.constant.OpenAiConfigProperties;
import org.naho.speech.llm.port.in.GetOpenAiCostInputPort;
import org.naho.speech.llm.port.in.SyncOpenAiCostInputPort;
import org.naho.speech.llm.port.out.OpenAiCostManagementPort;
import org.naho.speech.llm.port.out.OpenAiCostRepositoryPort;
import org.naho.speech.llm.usecase.OpenAiCostUseCase;
import org.naho.speech.llm.usecase.SyncOpenAiCostUseCase;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(OpenAiConfigProperties.class)
public class OpenAiCostConfig {

    @Bean
    public GetOpenAiCostInputPort getOpenAiCostInputPort(OpenAiCostRepositoryPort openAiCostRepositoryPort) {
        return new OpenAiCostUseCase(openAiCostRepositoryPort);
    }

    @Bean
    public SyncOpenAiCostInputPort syncOpenAiCostInputPort(OpenAiCostManagementPort openAiCostManagementPort,
                                                           OpenAiCostRepositoryPort openAiCostRepositoryPort) {
        return new SyncOpenAiCostUseCase(openAiCostManagementPort, openAiCostRepositoryPort);
    }
}
