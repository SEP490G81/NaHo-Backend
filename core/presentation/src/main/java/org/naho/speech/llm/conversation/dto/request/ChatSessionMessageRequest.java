package org.naho.speech.llm.conversation.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.naho.i18n.message.llm.LlmTitleMessageKey;

/**
 * HTTP Request DTO: Gửi text message trong speaking session.
 */
public record ChatSessionMessageRequest(
        @NotBlank(message = LlmTitleMessageKey.LLM_TRANSCRIPT_BLANK_TITLE)
        String transcript
) {
}
