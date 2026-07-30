package org.naho.speech.llm.dto.request;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

/**
 * HTTP Request DTO: Bắt đầu 1-1 conversation với Persona.
 * Cho phép tùy chọn truyền override level và kính ngữ (formality) từ UI Dropdown.
 */
public record StartConversationRequest(
        FormalityLevel formalityLevel,
        MarugotoLevel marugotoLevel
) {
}
