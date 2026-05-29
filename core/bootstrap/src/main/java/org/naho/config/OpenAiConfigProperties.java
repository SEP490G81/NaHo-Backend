package org.naho.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Mutable POJO: Spring Boot tự động bind properties từ prefix "openai" vào đây.
 * Dùng ở tầng bootstrap vì đây là configuration class phụ thuộc Spring.
 */
@Getter
@Setter
public class OpenAiConfigProperties {
    private String apiKey;
    private String chatModel = "gpt-4.1-mini";
    private String scoringModel = "gpt-4o";
    private int maxTokens = 1000;
    private double temperature = 0.7;
}
