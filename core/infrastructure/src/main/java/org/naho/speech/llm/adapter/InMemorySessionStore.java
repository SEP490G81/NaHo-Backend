package org.naho.speech.llm.adapter;

import org.naho.speech.llm.port.out.SessionStorePort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemorySessionStore implements SessionStorePort {

    private final ConcurrentHashMap<String, StringBuilder> transcripts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> topics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Map<String, String>>> histories = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> personaContexts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> voiceNames = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Long> userIds = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> sessionTypes = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> personaIds = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> startedAts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> marugotoLevels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> formalityLevels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> turnCounts = new ConcurrentHashMap<>();

    @Override
    public void initSession(String sessionId) {
        transcripts.put(sessionId, new StringBuilder());
        histories.put(sessionId, Collections.synchronizedList(new ArrayList<>()));
        turnCounts.put(sessionId, new AtomicInteger(0));
        startedAts.put(sessionId, Instant.now());
        System.out.println("[SessionStore] Session initialized: " + sessionId
                + " | Active sessions: " + transcripts.size());
    }

    @Override
    public void clearSession(String sessionId) {
        transcripts.remove(sessionId);
        topics.remove(sessionId);
        histories.remove(sessionId);
        personaContexts.remove(sessionId);
        voiceNames.remove(sessionId);
        userIds.remove(sessionId);
        sessionTypes.remove(sessionId);
        personaIds.remove(sessionId);
        startedAts.remove(sessionId);
        marugotoLevels.remove(sessionId);
        formalityLevels.remove(sessionId);
        turnCounts.remove(sessionId);
        System.out.println("[SessionStore] Session cleared: " + sessionId
                + " | Remaining sessions: " + transcripts.size());
    }

    @Override
    public void setTopic(String sessionId, String topic) {
        topics.put(sessionId, topic);
    }

    @Override
    public String getTopic(String sessionId) {
        return topics.getOrDefault(sessionId, "");
    }

    @Override
    public void addMessage(String sessionId, String role, String content) {
        List<Map<String, String>> history = histories.computeIfAbsent(
                sessionId, k -> Collections.synchronizedList(new ArrayList<>()));
        history.add(Map.of("role", role, "content", content));
    }

    @Override
    public List<Map<String, String>> getConversationHistory(String sessionId) {
        List<Map<String, String>> history = histories.get(sessionId);
        if (history == null) {
            return List.of();
        }
        synchronized (history) {
            return new ArrayList<>(history);
        }
    }

    @Override
    public void appendTranscript(String sessionId, String turn) {
        transcripts.computeIfAbsent(sessionId, k -> new StringBuilder())
                .append(turn)
                .append("\n");
    }

    @Override
    public String getFullTranscript(String sessionId) {
        StringBuilder sb = transcripts.get(sessionId);
        return (sb != null) ? sb.toString() : "";
    }

    @Override
    public void setPersonaContext(String sessionId, String personaContext) {
        if (personaContext != null) {
            personaContexts.put(sessionId, personaContext);
        }
    }

    @Override
    public String getPersonaContext(String sessionId) {
        return personaContexts.getOrDefault(sessionId, "");
    }

    @Override
    public void setVoiceName(String sessionId, String voiceName) {
        if (voiceName != null) {
            voiceNames.put(sessionId, voiceName);
        }
    }

    @Override
    public String getVoiceName(String sessionId) {
        return voiceNames.getOrDefault(sessionId, "ja-JP-NanamiNeural");
    }

    // ─── Metadata for DB persistence ─────────────────────────────

    @Override
    public void setUserId(String sessionId, Long userId) {
        if (userId != null)
            userIds.put(sessionId, userId);
    }

    @Override
    public Long getUserId(String sessionId) {
        return userIds.get(sessionId);
    }

    @Override
    public void setSessionType(String sessionId, String sessionType) {
        if (sessionType != null)
            sessionTypes.put(sessionId, sessionType);
    }

    @Override
    public String getSessionType(String sessionId) {
        return sessionTypes.getOrDefault(sessionId, "FREE_TALK");
    }

    @Override
    public void setPersonaId(String sessionId, Long personaId) {
        if (personaId != null)
            personaIds.put(sessionId, personaId);
    }

    @Override
    public Long getPersonaId(String sessionId) {
        return personaIds.get(sessionId);
    }

    @Override
    public void setStartedAt(String sessionId, Instant startedAt) {
        if (startedAt != null)
            startedAts.put(sessionId, startedAt);
    }

    @Override
    public Instant getStartedAt(String sessionId) {
        return startedAts.getOrDefault(sessionId, Instant.now());
    }

    @Override
    public void setMarugotoLevel(String sessionId, String marugotoLevel) {
        if (marugotoLevel != null)
            marugotoLevels.put(sessionId, marugotoLevel);
    }

    @Override
    public String getMarugotoLevel(String sessionId) {
        return marugotoLevels.get(sessionId);
    }

    @Override
    public void setFormalityLevel(String sessionId, String formalityLevel) {
        if (formalityLevel != null)
            formalityLevels.put(sessionId, formalityLevel);
    }

    @Override
    public String getFormalityLevel(String sessionId) {
        return formalityLevels.get(sessionId);
    }

    @Override
    public int incrementTurnCount(String sessionId) {
        return turnCounts.computeIfAbsent(sessionId, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public int getTurnCount(String sessionId) {
        AtomicInteger count = turnCounts.get(sessionId);
        return count != null ? count.get() : 0;
    }
}
