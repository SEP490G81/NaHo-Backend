package org.naho.speech.azure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "azure.speech")
public class AzureSpeechProperties {
    private String subscriptionKey;
    private String region;
    private String language;
}