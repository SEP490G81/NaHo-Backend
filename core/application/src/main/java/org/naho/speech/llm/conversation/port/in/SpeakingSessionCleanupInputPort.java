package org.naho.speech.llm.conversation.port.in;

public interface SpeakingSessionCleanupInputPort {
    void deleteSession(String sessionCode, Long userId);
}
