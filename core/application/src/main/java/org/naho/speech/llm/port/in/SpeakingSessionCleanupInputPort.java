package org.naho.speech.llm.port.in;

public interface SpeakingSessionCleanupInputPort {
    int cleanupExpiredSessions(int expireAfterHours);
}
