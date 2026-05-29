package org.naho.speech.llm.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * HTTP Request DTO: Bắt đầu speaking session theo chủ đề.
 */
public record StartTopicRequest(
        @NotBlank(message = "Topic không được để trống")
        String topic
) {
}
