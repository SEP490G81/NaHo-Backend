package org.naho.speech.llm.conversation.port.out;

import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;

public interface AiScoringPort {
    SpeakingSessionAssessmentResult score(
            String sessionCode,
            String topic,
            String systemPromptContent,
            String messagesJson
    );
}
