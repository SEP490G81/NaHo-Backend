package org.naho.speech.azure.constant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.azure")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class AzureSpeechConfigProperties {
    String subscriptionKey;
    String region;
    String language;
}