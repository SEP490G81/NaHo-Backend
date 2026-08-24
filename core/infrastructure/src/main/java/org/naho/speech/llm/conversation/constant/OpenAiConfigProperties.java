package org.naho.speech.llm.conversation.constant;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.openai")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OpenAiConfigProperties {
    String apiKey;
    String adminApiKey;
    String chatModel;
    String scoringModel;
    Integer maxTokens;
    Double lowTemperature;
    Double highTemperature;
    Duration requestTimeout;
}
