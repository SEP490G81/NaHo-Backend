package org.naho.speech.llm.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface SessionStorePort {
    void initSession(String sessionId);

    boolean hasSession(String sessionId);

    void clearSession(String sessionId);

    void restoreSession(String sessionId, Long userId, Long personaId, String topic, String marugotoLevel, String formalityLevel, String fullTranscript, int totalTurns, Instant startedAt, List<Map<String, String>> historyMessages);

    //Topic Management
    void setTopic(String sessionId, String topic);

    String getTopic(String sessionId);

    //Conversation History
    void addMessage(String sessionId, String role, String content);

    List<Map<String, String>> getConversationHistory(String sessionId);

    //Transcript
    void appendTranscript(String sessionId, String turn);

    String getFullTranscript(String sessionId);

    // Persona / Conversation Style context for scoring
    void setPersonaContext(String sessionId, String personaContext);

    String getPersonaContext(String sessionId);

    // Voice name for TTS replies
    void setVoiceName(String sessionId, String voiceName);

    String getVoiceName(String sessionId);

    // Session metadata for DB persistence
    void setUserId(String sessionId, Long userId);

    Long getUserId(String sessionId);

    void setPersonaId(String sessionId, Long personaId);

    Long getPersonaId(String sessionId);

    void setStartedAt(String sessionId, Instant startedAt);

    Instant getStartedAt(String sessionId);

    void setMarugotoLevel(String sessionId, String marugotoLevel);

    String getMarugotoLevel(String sessionId);

    void setFormalityLevel(String sessionId, String formalityLevel);

    String getFormalityLevel(String sessionId);

    int incrementTurnCount(String sessionId);

    int getTurnCount(String sessionId);
}

