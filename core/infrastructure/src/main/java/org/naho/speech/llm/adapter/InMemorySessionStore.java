package org.naho.speech.llm.adapter;

import org.naho.speech.llm.port.out.SessionStorePort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemorySessionStore implements SessionStorePort {

    private final ConcurrentHashMap<String, StringBuilder> transcripts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> topics = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, List<Map<String, String>>> histories = new ConcurrentHashMap<>();

    @Override
    public void initSession(String sessionId) {
        transcripts.put(sessionId, new StringBuilder());
        histories.put(sessionId, Collections.synchronizedList(new ArrayList<>()));
        System.out.println("[SessionStore] Session initialized: " + sessionId
                + " | Active sessions: " + transcripts.size());
    }

    @Override
    public void clearSession(String sessionId) {
        transcripts.remove(sessionId);
        topics.remove(sessionId);
        histories.remove(sessionId);
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
}
