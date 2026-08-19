package org.naho.speech.llm.conversation.dto.request;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

public record StartConversationRequest(
        FormalityLevel formalityLevel,
        MarugotoLevel marugotoLevel
) {
}
