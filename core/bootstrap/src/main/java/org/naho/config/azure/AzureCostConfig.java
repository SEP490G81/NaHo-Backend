package org.naho.config.azure;

import org.naho.speech.azure.config.AzureCostConfigProperties;
import org.naho.speech.azure.port.in.GetAzureCostInputPort;
import org.naho.speech.azure.port.in.SyncAzureCostInputPort;
import org.naho.speech.azure.port.out.AzureCostManagementPort;
import org.naho.speech.azure.port.out.AzureCostRepositoryPort;
import org.naho.speech.azure.usecase.AzureCostUseCase;
import org.naho.speech.azure.usecase.SyncAzureCostUseCase;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(AzureCostConfigProperties.class)
public class AzureCostConfig {

    @Bean
    public GetAzureCostInputPort getAzureCostInputPort(AzureCostRepositoryPort azureCostRepositoryPort) {
        return new AzureCostUseCase(azureCostRepositoryPort);
    }

    @Bean
    public SyncAzureCostInputPort syncAzureCostInputPort(AzureCostManagementPort azureCostManagementPort,
                                                         AzureCostRepositoryPort azureCostRepositoryPort) {
        return new SyncAzureCostUseCase(azureCostManagementPort, azureCostRepositoryPort);
    }
}
