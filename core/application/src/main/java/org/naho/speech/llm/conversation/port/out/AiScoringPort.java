package org.naho.speech.llm.conversation.port.out;

import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;

public interface AiScoringPort {
    SpeakingSessionAssessment score(
            String sessionCode,
            String topic,
            String systemPromptContent,
            String messagesJson
    );
}
