package org.naho.speech.llm.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * HTTP Request DTO: Gửi text message trong speaking session.
 */
public record ChatSessionMessageRequest(
        @NotBlank(message = "transcript không được để trống")
        String transcript
) {
}
