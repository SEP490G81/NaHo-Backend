package org.naho.speech.llm.conversation.port.in;

import org.naho.speech.llm.conversation.result.SpeakingSessionResult;

public interface EndSessionInputPort {
    SpeakingSessionResult endSession(Long userId, String sessionCode);

    SpeakingSessionResult doEndSession(Long userId, String sessionCode);
}

