package org.naho.speech.llm.adapter;

import org.naho.persona.type.FormalityLevel;
import org.naho.persona.type.MarugotoLevel;
import org.naho.speech.llm.port.out.SessionStorePort;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemorySessionStore implements SessionStorePort {

    private final ConcurrentHashMap<String, StringBuilder> transcripts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> topics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Map<String, String>>> histories = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> personaContexts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> voiceNames = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, Long> userIds = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> personaIds = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Instant> startedAts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, MarugotoLevel> marugotoLevels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, FormalityLevel> formalityLevels = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicInteger> turnCounts = new ConcurrentHashMap<>();

    // Track last activity time per session — dùng cho eviction
    private final ConcurrentHashMap<String, Instant> lastAccessTimes = new ConcurrentHashMap<>();

    @Override
    public void initSession(String sessionCode) {
        transcripts.put(sessionCode, new StringBuilder());
        histories.put(sessionCode, Collections.synchronizedList(new ArrayList<>()));
        turnCounts.put(sessionCode, new AtomicInteger(0));
        startedAts.put(sessionCode, Instant.now());
        lastAccessTimes.put(sessionCode, Instant.now());
        System.out.println("[SessionStore] Session initialized: " + sessionCode
                + " | Active sessions: " + transcripts.size());
    }

    @Override
    public boolean hasSession(String sessionCode) {
        return sessionCode != null && transcripts.containsKey(sessionCode);
    }

    @Override
    public void restoreSession(String sessionCode, Long userId, Long personaId, String topic, MarugotoLevel marugotoLevel, FormalityLevel formalityLevel, String fullTranscript, int totalTurns, Instant startedAt, List<Map<String, String>> historyMessages) {
        transcripts.put(sessionCode, new StringBuilder(fullTranscript != null ? fullTranscript : ""));
        histories.put(sessionCode, Collections.synchronizedList(new ArrayList<>(historyMessages != null ? historyMessages : List.of())));
        turnCounts.put(sessionCode, new AtomicInteger(totalTurns));
        if (startedAt != null) startedAts.put(sessionCode, startedAt);
        if (userId != null) userIds.put(sessionCode, userId);
        if (personaId != null) personaIds.put(sessionCode, personaId);
        if (topic != null) topics.put(sessionCode, topic);
        if (marugotoLevel != null) marugotoLevels.put(sessionCode, marugotoLevel);
        if (formalityLevel != null) formalityLevels.put(sessionCode, formalityLevel);
        voiceNames.put(sessionCode, "ja-JP-NanamiNeural");
        lastAccessTimes.put(sessionCode, Instant.now());
        System.out.println("[SessionStore] Session restored from DB: " + sessionCode + " | Turns: " + totalTurns);
    }

    @Override
    public void clearSession(String sessionCode) {
        transcripts.remove(sessionCode);
        topics.remove(sessionCode);
        histories.remove(sessionCode);
        personaContexts.remove(sessionCode);
        voiceNames.remove(sessionCode);
        userIds.remove(sessionCode);
        personaIds.remove(sessionCode);
        startedAts.remove(sessionCode);
        marugotoLevels.remove(sessionCode);
        formalityLevels.remove(sessionCode);
        turnCounts.remove(sessionCode);
        lastAccessTimes.remove(sessionCode);
        System.out.println("[SessionStore] Session cleared: " + sessionCode
                + " | Remaining sessions: " + transcripts.size());
    }

    @Override
    public void setTopic(String sessionCode, String topic) {
        topics.put(sessionCode, topic);
    }

    @Override
    public String getTopic(String sessionCode) {
        return topics.getOrDefault(sessionCode, "");
    }

    @Override
    public void addMessage(String sessionCode, String role, String content) {
        List<Map<String, String>> history = histories.computeIfAbsent(
                sessionCode, k -> Collections.synchronizedList(new ArrayList<>()));
        history.add(Map.of("role", role, "content", content));
        lastAccessTimes.put(sessionCode, Instant.now()); // cập nhật activity
    }

    @Override
    public List<Map<String, String>> getConversationHistory(String sessionCode) {
        List<Map<String, String>> history = histories.get(sessionCode);
        if (history == null) {
            return List.of();
        }
        lastAccessTimes.put(sessionCode, Instant.now()); // cập nhật activity
        synchronized (history) {
            return new ArrayList<>(history);
        }
    }

    @Override
    public void appendTranscript(String sessionCode, String turn) {
        transcripts.computeIfAbsent(sessionCode, k -> new StringBuilder())
                .append(turn)
                .append("\n");
    }

    @Override
    public String getFullTranscript(String sessionCode) {
        StringBuilder sb = transcripts.get(sessionCode);
        return (sb != null) ? sb.toString() : "";
    }

    @Override
    public void setPersonaContext(String sessionCode, String personaContext) {
        if (personaContext != null) {
            personaContexts.put(sessionCode, personaContext);
        }
    }

    @Override
    public String getPersonaContext(String sessionCode) {
        return personaContexts.getOrDefault(sessionCode, "");
    }

    @Override
    public void setVoiceName(String sessionCode, String voiceName) {
        if (voiceName != null) {
            voiceNames.put(sessionCode, voiceName);
        }
    }

    @Override
    public String getVoiceName(String sessionCode) {
        return voiceNames.getOrDefault(sessionCode, "ja-JP-NanamiNeural");
    }

    // ─── Metadata for DB persistence ─────────────────────────────

    @Override
    public void setUserId(String sessionCode, Long userId) {
        if (userId != null)
            userIds.put(sessionCode, userId);
    }

    @Override
    public Long getUserId(String sessionCode) {
        return userIds.get(sessionCode);
    }

    @Override
    public void setPersonaId(String sessionCode, Long personaId) {
        if (personaId != null)
            personaIds.put(sessionCode, personaId);
    }

    @Override
    public Long getPersonaId(String sessionCode) {
        return personaIds.get(sessionCode);
    }

    @Override
    public void setStartedAt(String sessionCode, Instant startedAt) {
        if (startedAt != null)
            startedAts.put(sessionCode, startedAt);
    }

    @Override
    public Instant getStartedAt(String sessionCode) {
        return startedAts.getOrDefault(sessionCode, Instant.now());
    }

    @Override
    public void setMarugotoLevel(String sessionCode, MarugotoLevel marugotoLevel) {
        if (marugotoLevel != null)
            marugotoLevels.put(sessionCode, marugotoLevel);
    }

    @Override
    public MarugotoLevel getMarugotoLevel(String sessionCode) {
        return marugotoLevels.get(sessionCode);
    }

    @Override
    public void setFormalityLevel(String sessionCode, FormalityLevel formalityLevel) {
        if (formalityLevel != null)
            formalityLevels.put(sessionCode, formalityLevel);
    }

    @Override
    public FormalityLevel getFormalityLevel(String sessionCode) {
        return formalityLevels.get(sessionCode);
    }

    @Override
    public int incrementTurnCount(String sessionCode) {
        lastAccessTimes.put(sessionCode, Instant.now()); // cập nhật activity khi user gửi message
        return turnCounts.computeIfAbsent(sessionCode, k -> new AtomicInteger(0)).incrementAndGet();
    }

    @Override
    public int getTurnCount(String sessionCode) {
        AtomicInteger count = turnCounts.get(sessionCode);
        return count != null ? count.get() : 0;
    }

    @Override
    public int evictIdleSessions(int idleMinutes) {
        Instant cutoff = Instant.now().minus(idleMinutes, ChronoUnit.MINUTES);
        List<String> toEvict = lastAccessTimes.entrySet().stream()
                .filter(e -> e.getValue().isBefore(cutoff))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        toEvict.forEach(this::clearSession);
        if (!toEvict.isEmpty()) {
            System.out.println("[SessionStore] Evicted " + toEvict.size()
                    + " idle sessions (>" + idleMinutes + " min) from memory");
        }
        return toEvict.size();
    }
}
