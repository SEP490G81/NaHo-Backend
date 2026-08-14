package org.naho.config.azure;

import org.naho.cost.constant.AzureCostConfigProperties;
import org.naho.cost.port.in.GetAzureCostInputPort;
import org.naho.cost.port.in.SyncAzureCostInputPort;
import org.naho.cost.port.out.AzureCostManagementPort;
import org.naho.cost.port.out.AzureCostRepositoryPort;
import org.naho.cost.usecase.AzureCostUseCase;
import org.naho.cost.usecase.SyncAzureCostUseCase;
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
