package org.naho.speech.llm.conversation.port.out;

import org.naho.speech.llm.model.conversation.SpeakingSessionAssessment;

public interface SpeakingSessionAssessmentRepositoryPort {
    SpeakingSessionAssessment save(SpeakingSessionAssessment speakingSessionAssessment);
}
