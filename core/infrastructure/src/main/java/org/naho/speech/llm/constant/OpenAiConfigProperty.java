package org.naho.speech.llm.constant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.openai")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OpenAiConfigProperty {
    String apiKey;
    String chatModel;
    String scoringModel;
    int maxTokens;
    double temperature;
}
