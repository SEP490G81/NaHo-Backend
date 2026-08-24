package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.result.SpeakingSessionAssessmentResult;

public interface EndSessionInputPort {
    SpeakingSessionAssessmentResult endSession(Long userId, String sessionCode);
}

