package org.naho.speech.llm.port.in;

public interface SpeakingSessionCleanupInputPort {
    void deleteSession(String sessionCode, Long userId);
}
