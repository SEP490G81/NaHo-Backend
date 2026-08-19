package org.naho.speech.llm.conversation.port.out;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface SessionStorePort {
    void initSession(String sessionCode);

    boolean hasSession(String sessionCode);

    void clearSession(String sessionCode);

    void restoreSession(String sessionCode, Long userId, Long personaId, String topic, MarugotoLevel marugotoLevel, FormalityLevel formalityLevel, String fullTranscript, int totalTurns, Instant startedAt, List<Map<String, String>> historyMessages);

    //Topic Management
    void setTopic(String sessionCode, String topic);

    String getTopic(String sessionCode);

    //Conversation History
    void addMessage(String sessionCode, String role, String content);

    List<Map<String, String>> getConversationHistory(String sessionCode);

    //Transcript
    void appendTranscript(String sessionCode, String turn);

    String getFullTranscript(String sessionCode);

    // Persona / Conversation Style context for scoring
    void setPersonaContext(String sessionCode, String personaContext);

    String getPersonaContext(String sessionCode);

    // Voice name for TTS replies
    void setVoiceName(String sessionCode, String voiceName);

    String getVoiceName(String sessionCode);

    // Session metadata for DB persistence
    void setUserId(String sessionCode, Long userId);

    Long getUserId(String sessionCode);

    void setPersonaId(String sessionCode, Long personaId);

    Long getPersonaId(String sessionCode);

    void setStartedAt(String sessionCode, Instant startedAt);

    Instant getStartedAt(String sessionCode);

    void setMarugotoLevel(String sessionCode, MarugotoLevel marugotoLevel);

    MarugotoLevel getMarugotoLevel(String sessionCode);

    void setFormalityLevel(String sessionCode, FormalityLevel formalityLevel);

    FormalityLevel getFormalityLevel(String sessionCode);

    int incrementTurnCount(String sessionCode);

    int getTurnCount(String sessionCode);

    /**
     * Xóa các session không hoạt động khỏi memory.
     * Phù hợp cho speaking model (session ngắn ~15-25 phút).
     *
     * @param idleMinutes số phút không có hoạt động để evict
     * @return số session đã bị evict
     */
    int evictIdleSessions(int idleMinutes);
}

