package org.naho.config.openai;

import org.naho.cost.port.in.SyncOpenAiCostInputPort;
import org.naho.cost.port.out.OpenAiCostManagementPort;
import org.naho.cost.port.out.OpenAiCostRepositoryPort;
import org.naho.cost.usecase.OpenAiCostUseCase;
import org.naho.speech.llm.conversation.constant.OpenAiConfigProperties;
import org.naho.speech.llm.conversation.port.in.GetOpenAiCostInputPort;
import org.naho.speech.llm.conversation.usecase.SyncOpenAiCostUseCase;
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
