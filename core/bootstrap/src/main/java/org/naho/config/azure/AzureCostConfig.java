package org.naho.config.azure;

import org.naho.speech.azure.config.AzureCostConfigProperties;
import org.naho.speech.azure.port.in.GetAzureCostInputPort;
import org.naho.speech.azure.port.out.AzureCostManagementPort;
import org.naho.speech.azure.usecase.AzureCostUseCase;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AzureCostConfigProperties.class)
public class AzureCostConfig {

    @Bean
    public GetAzureCostInputPort getAzureCostInputPort(AzureCostManagementPort azureCostManagementPort) {
        return new AzureCostUseCase(azureCostManagementPort);
    }
}
