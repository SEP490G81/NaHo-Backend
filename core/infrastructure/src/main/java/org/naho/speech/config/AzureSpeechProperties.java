package org.naho.speech.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "azure.speech")
public class AzureSpeechProperties {
    private String subscriptionKey;
    private String region;
    private String language = "ja-JP";
}